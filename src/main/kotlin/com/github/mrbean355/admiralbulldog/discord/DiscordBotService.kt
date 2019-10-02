package com.github.mrbean355.admiralbulldog.discord

import com.github.mrbean355.admiralbulldog.assets.SoundFile
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

private const val HOST_URL_PROD = "http://roonsbot.co.za:8090"
private const val HOST_URL_DEV = "http://localhost:1234"
private const val HOST_URL = HOST_URL_PROD

/**
 * Play the given [soundFile] on Discord through the bot.
 * Can pass in a custom [token] to be used instead of loading one from config.
 */
fun playSoundOnDiscord(soundFile: SoundFile, token: String = ConfigPersistence.getDiscordToken()) {
    if (token.isBlank()) {
        println("Blank token set!")
        return
    }
    GlobalScope.launch {
        val fileName = soundFile.path.substringAfterLast('/')
        val response = service.playSound(PlaySoundRequest(loadUserId(), token, fileName))
        if (response.isSuccessful) {
            println("Sound played! soundFile='$soundFile'")
        } else {
            println("Play sound failed! soundFile='$soundFile', code=${response.code()}")
        }
    }
}

/**
 * Log an analytics event.
 */
fun logAnalyticsEvent(eventType: String, eventData: String = "") {
    GlobalScope.launch {
        val response = service.logAnalyticsEvent(AnalyticsRequest(loadUserId(), eventType, eventData))
        if (response.isSuccessful) {
            println("Analytics sent! eventType='$eventType', eventData='$eventData'")
        } else {
            println("Analytics failed! code=${response.code()}")
        }
    }
}

private interface DiscordBotService {

    @POST("/createId")
    suspend fun createId(): Response<CreateIdResponse>

    @POST("/")
    suspend fun playSound(@Body request: PlaySoundRequest): Response<Void>

    @POST("/analytics/logEvent")
    suspend fun logAnalyticsEvent(@Body request: AnalyticsRequest): Response<Void>
}

private data class CreateIdResponse(val userId: String)

private data class AnalyticsRequest(val userId: String, val eventType: String, val eventData: String)

private data class PlaySoundRequest(val userId: String, val token: String, val soundFileName: String)

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
            val response = service.createId()
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

private val mutex = Mutex()
private val service = Retrofit.Builder()
        .client(OkHttpClient.Builder()
                .callTimeout(10, TimeUnit.SECONDS)
                .build())
        .baseUrl(HOST_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(DiscordBotService::class.java)
