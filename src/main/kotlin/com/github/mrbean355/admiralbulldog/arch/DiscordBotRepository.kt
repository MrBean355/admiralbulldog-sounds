package com.github.mrbean355.admiralbulldog.arch

import com.github.mrbean355.admiralbulldog.assets.SoundBite
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

var hostUrl = "http://prod.upmccxmkjx.us-east-2.elasticbeanstalk.com:8090"

class DiscordBotRepository {

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