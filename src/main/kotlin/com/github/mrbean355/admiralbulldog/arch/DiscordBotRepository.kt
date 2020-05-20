package com.github.mrbean355.admiralbulldog.arch

import com.github.mrbean355.admiralbulldog.assets.SoundBite
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import com.github.mrbean355.admiralbulldog.ui2.persistance.AppConfig
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.FileOutputStream

var hostUrl = "http://prod.upmccxmkjx.us-east-2.elasticbeanstalk.com:8090"

class DiscordBotRepository {

    suspend fun sendHeartbeat() {
        callService { DiscordBotService.INSTANCE.heartbeat(loadUserId()) }
    }

    suspend fun listSoundBites(): ServiceResponse<Map<String, String>> {
        return callService { DiscordBotService.INSTANCE.listSoundBites() }
                .toServiceResponse()
    }

    suspend fun downloadSoundBite(name: String, destination: String): ServiceResponse<Any> {
        val response = callService { DiscordBotService.INSTANCE.downloadSoundBite(name) }
        val responseBody = response.body()
        if (response.isSuccessful && responseBody != null) {
            responseBody.byteStream().use { input ->
                FileOutputStream("$destination/$name").use { output ->
                    input.copyTo(output)
                }
            }
        }
        return response.toServiceResponse { Any() }
    }

    suspend fun lookUpToken(token: String): ServiceResponse<String> {
        val response = callService { DiscordBotService.INSTANCE.lookUpToken(token) }
        return response.toServiceResponse {
            it.charStream().readText()
        }
    }

    suspend fun playSound(soundBite: SoundBite): ServiceResponse<Void> {
        return callService { DiscordBotService.INSTANCE.playSound(PlaySoundRequest(loadUserId(), ConfigPersistence.getDiscordToken(), soundBite.fileName)) }
                .toServiceResponse()
    }

    suspend fun playSoundNew(fileName: String): ServiceResponse<Void> {
        // TODO: Implement user ID
        return callService { DiscordBotService.INSTANCE.playSound(PlaySoundRequest("TODO", AppConfig.discordMagicNumberProperty().get(), fileName)) }
                .toServiceResponse()
    }

    suspend fun logAnalyticsEvent(eventType: String, eventData: String): ServiceResponse<Void> {
        return callService { DiscordBotService.INSTANCE.logAnalyticsEvent(AnalyticsRequest(loadUserId(), eventType, eventData)) }
                .toServiceResponse()
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
                val response = callService { DiscordBotService.INSTANCE.createId() }
                if (response.isSuccessful) {
                    response.body()?.userId.orEmpty().also {
                        ConfigPersistence.setId(it)
                    }
                } else {
                    ""
                }
            }
        }
    }

    private companion object {
        private val mutex = Mutex()
    }
}