package com.github.mrbean355.admiralbulldog.service

import com.github.mrbean355.admiralbulldog.APP_VERSION
import com.github.mrbean355.admiralbulldog.assets.SoundFile
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import org.slf4j.LoggerFactory
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

var hostUrl = "http://prod.upmccxmkjx.us-east-2.elasticbeanstalk.com:8090"
private val logger = LoggerFactory.getLogger("DiscordBotService")

/**
 * Play the given [soundFile] on Discord through the bot.
 * Can pass in a custom [token] to be used instead of loading one from config.
 */
fun playSoundOnDiscord(soundFile: SoundFile, token: String = ConfigPersistence.getDiscordToken()) {
    if (token.isBlank()) {
        logger.warn("Blank token set!")
        return
    }
    GlobalScope.launch {
        val response = service.playSound(PlaySoundRequest(loadUserId(), token, soundFile.fileName))
        if (!response.isSuccessful) {
            logger.info("Play sound through Discord failed! soundFile=$soundFile, response=$response")
            soundFile.play()
        }
    }
}

/**
 * Log an analytics event.
 */
fun logAnalyticsEvent(eventType: String, eventData: String = "") {
    GlobalScope.launch {
        service.logAnalyticsEvent(AnalyticsRequest(loadUserId(), eventType, eventData))
    }
}

/**
 * Invokes [onLaterVersion] if the service says there's a newer app version available.
 */
fun whenLaterVersionAvailable(onLaterVersion: () -> Unit) {
    GlobalScope.launch {
        val response = service.hasLaterVersion(APP_VERSION)
        if (response.isSuccessful) {
            val body = response.body() ?: false
            if (body) {
                withContext(Main) { onLaterVersion() }
            }
        } else {
            logger.error("Later version check failed! response=$response")
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

    @GET("/metadata/laterVersion")
    suspend fun hasLaterVersion(@Query("version") version: String): Response<Boolean>
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
private val service by lazy {
    Retrofit.Builder()
            .client(OkHttpClient.Builder()
                    .callTimeout(10, TimeUnit.SECONDS)
                    .build())
            .baseUrl(hostUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DiscordBotService::class.java)
}
