/*
 * Copyright 2021 Michael Johnston
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.mrbean355.admiralbulldog.arch.repo

import com.github.mrbean355.admiralbulldog.arch.AnalyticsRequest
import com.github.mrbean355.admiralbulldog.arch.PlaySoundRequest
import com.github.mrbean355.admiralbulldog.arch.PlaySoundsRequest
import com.github.mrbean355.admiralbulldog.arch.ServiceResponse
import com.github.mrbean355.admiralbulldog.arch.SoundToPlay
import com.github.mrbean355.admiralbulldog.arch.service.DiscordBotService
import com.github.mrbean355.admiralbulldog.arch.toServiceResponse
import com.github.mrbean355.admiralbulldog.assets.ComboSoundBite
import com.github.mrbean355.admiralbulldog.assets.SingleSoundBite
import com.github.mrbean355.admiralbulldog.assets.SoundBite
import com.github.mrbean355.admiralbulldog.common.DEFAULT_INDIVIDUAL_VOLUME
import com.github.mrbean355.admiralbulldog.common.DEFAULT_RATE
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import java.io.FileOutputStream

var hostUrl = "http://prod.upmccxmkjx.us-east-2.elasticbeanstalk.com:8090"

class DiscordBotRepository {
    private val logger = LoggerFactory.getLogger(DiscordBotRepository::class.java)

    suspend fun sendHeartbeat(): Unit = withContext(IO) {
        try {
            DiscordBotService.INSTANCE.heartbeat(loadUserId())
        } catch (t: Throwable) {
            logger.error("Failed to send heartbeat", t)
        }
    }

    suspend fun listSoundBites(): ServiceResponse<Map<String, String>> {
        return try {
            DiscordBotService.INSTANCE.listSoundBites().toServiceResponse()
        } catch (t: Throwable) {
            logger.error("Failed to list sound bites", t)
            ServiceResponse.Exception()
        }
    }

    suspend fun downloadSoundBite(name: String, destination: String): ServiceResponse<Any> {
        return try {
            val response = DiscordBotService.INSTANCE.downloadSoundBite(name)
            val responseBody = response.body()
            if (response.isSuccessful && responseBody != null) {
                responseBody.byteStream().use { input ->
                    FileOutputStream("$destination/$name").use { output ->
                        input.copyTo(output)
                    }
                }
            }
            return response.toServiceResponse { Any() }
        } catch (t: Throwable) {
            logger.error("Failed to download sound bite: $name", t)
            ServiceResponse.Exception()
        }
    }

    suspend fun lookUpToken(token: String): ServiceResponse<String> = withContext(IO) {
        try {
            DiscordBotService.INSTANCE.lookUpToken(token)
                    .toServiceResponse { it.charStream().readText() }
        } catch (t: Throwable) {
            logger.error("Failed to look up token", t)
            ServiceResponse.Exception()
        }
    }

    suspend fun playSound(soundBite: SoundBite, rate: Int = DEFAULT_RATE): ServiceResponse<Void> = withContext(IO) {
        when (soundBite) {
            is SingleSoundBite -> playSingleSound(soundBite, rate)
            is ComboSoundBite -> playMultipleSounds(soundBite, rate)
        }
    }

    suspend fun logAnalyticsProperties(properties: Map<String, Any>): ServiceResponse<Void> = withContext(IO) {
        try {
            DiscordBotService.INSTANCE.logAnalyticsProperties(AnalyticsRequest(loadUserId(), properties.mapValues { it.value.toString() }))
                    .toServiceResponse()
        } catch (t: Throwable) {
            logger.error("Failed to log analytics event", t)
            ServiceResponse.Exception()
        }
    }

    private suspend fun playSingleSound(soundBite: SingleSoundBite, rate: Int): ServiceResponse<Void> {
        val volume = ConfigPersistence.getSoundBiteVolume(soundBite.name) ?: DEFAULT_INDIVIDUAL_VOLUME
        return try {
            DiscordBotService.INSTANCE.playSound(PlaySoundRequest(loadUserId(), ConfigPersistence.getDiscordToken(), soundBite.fileName, volume, rate))
                    .toServiceResponse()
        } catch (t: Throwable) {
            logger.error("Failed to play sound through Discord: $soundBite", t)
            ServiceResponse.Exception()
        }
    }

    private suspend fun playMultipleSounds(soundBite: ComboSoundBite, rate: Int): ServiceResponse<Void> {
        val sounds = soundBite.getSoundBites().filterIsInstance<SingleSoundBite>().map {
            SoundToPlay(it.fileName, ConfigPersistence.getSoundBiteVolume(it.name) ?: DEFAULT_INDIVIDUAL_VOLUME, rate)
        }
        return try {
            DiscordBotService.INSTANCE.playSounds(PlaySoundsRequest(loadUserId(), ConfigPersistence.getDiscordToken(), sounds))
                    .toServiceResponse()
        } catch (t: Throwable) {
            logger.error("Failed to play combo sound through Discord: $soundBite", t)
            ServiceResponse.Exception()
        }
    }

    private suspend fun loadUserId(): String {
        val userId = ConfigPersistence.getId()
        if (userId.isNotEmpty()) {
            return userId
        }
        return mutex.withLock {
            val userId2 = ConfigPersistence.getId()
            if (userId.isNotEmpty()) {
                userId2
            } else {
                try {
                    val response = DiscordBotService.INSTANCE.createId()
                    if (response.isSuccessful) {
                        response.body()?.userId.orEmpty().also {
                            ConfigPersistence.setId(it)
                        }
                    } else {
                        ""
                    }
                } catch (t: Throwable) {
                    logger.error("Failed to create ID", t)
                    ""
                }
            }
        }
    }

    private companion object {
        private val mutex = Mutex()
    }
}