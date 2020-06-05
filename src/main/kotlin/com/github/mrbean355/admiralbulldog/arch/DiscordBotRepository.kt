package com.github.mrbean355.admiralbulldog.arch

import com.github.mrbean355.admiralbulldog.assets.SoundBite
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.slf4j.LoggerFactory
import java.io.FileOutputStream

var hostUrl = "http://prod.upmccxmkjx.us-east-2.elasticbeanstalk.com:8090"

class DiscordBotRepository {
    private val logger = LoggerFactory.getLogger(DiscordBotRepository::class.java)

    suspend fun sendHeartbeat() {
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

    suspend fun lookUpToken(token: String): ServiceResponse<String> {
        return try {
            DiscordBotService.INSTANCE.lookUpToken(token)
                    .toServiceResponse { it.charStream().readText() }
        } catch (t: Throwable) {
            logger.error("Failed to look up token", t)
            ServiceResponse.Exception()
        }
    }

    suspend fun playSound(soundBite: SoundBite): ServiceResponse<Void> {
        return try {
            DiscordBotService.INSTANCE.playSound(PlaySoundRequest(loadUserId(), ConfigPersistence.getDiscordToken(), soundBite.fileName))
                    .toServiceResponse()
        } catch (t: Throwable) {
            logger.error("Failed to play sound through Discord: $soundBite", t)
            ServiceResponse.Exception()
        }
    }

    suspend fun logAnalyticsEvent(eventType: String, eventData: String): ServiceResponse<Void> {
        return try {
            DiscordBotService.INSTANCE.logAnalyticsEvent(AnalyticsRequest(loadUserId(), eventType, eventData))
                    .toServiceResponse()
        } catch (t: Throwable) {
            logger.error("Failed to log analytics event", t)
            ServiceResponse.Exception()
        }
    }

    private suspend fun loadUserId(): String {
        val userId = ConfigPersistence.getId()
        if (userId != null) {
            return userId
        }
        return mutex.withLock {
            val userId2 = ConfigPersistence.getId()
            if (userId2 != null) {
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