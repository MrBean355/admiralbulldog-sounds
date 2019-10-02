package com.github.mrbean355.admiralbulldog.discord

import com.github.mrbean355.admiralbulldog.assets.SoundFile
import com.github.mrbean355.admiralbulldog.bytes.OnBountyRunesSpawn
import com.github.mrbean355.admiralbulldog.bytes.SoundByte
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

private const val HOST_URL_PROD = "http://roonsbot.co.za:8090"
private const val HOST_URL_DEV = "http://localhost:1234"
private const val HOST_URL = HOST_URL_PROD

fun createId(success: () -> Unit) {
    val service = Retrofit.Builder()
            .client(OkHttpClient.Builder()
                    .callTimeout(10, TimeUnit.SECONDS)
                    .build())
            .baseUrl(HOST_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DiscordBotService::class.java)

    GlobalScope.launch {
        val response = service.createId()
        if (response.isSuccessful) {
            ConfigPersistence.setId(response.body()?.userId.orEmpty())
            withContext(Dispatchers.Main) {
                success()
            }
        }
    }
}

fun shouldPlayOnDiscord(soundByte: SoundByte): Boolean {
    return ConfigPersistence.isUsingDiscordBot() && soundByte is OnBountyRunesSpawn
}

fun playSoundOnDiscord(soundFile: SoundFile, token: String = ConfigPersistence.getDiscordToken()) {
    val service = Retrofit.Builder()
            .client(OkHttpClient.Builder()
                    .callTimeout(10, TimeUnit.SECONDS)
                    .build())
            .baseUrl(HOST_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DiscordBotService::class.java)

    if (token.isBlank()) {
        println("Blank token set!")
        return
    }
    val fileName = soundFile.path.substringAfterLast('/')
    service.playSound(PlaySoundRequest(ConfigPersistence.getId().orEmpty(), token, fileName)).enqueue(object : Callback<Void> {

        override fun onResponse(call: Call<Void>, response: Response<Void>) {
            if (response.isSuccessful) {
                println("Sound played! soundFile='$soundFile'")
            } else {
                println("Play sound failed! code=${response.code()}")
            }
        }

        override fun onFailure(call: Call<Void>, t: Throwable) {
            println("Play sound failed! exception=$t")
        }
    })
}

fun logAnalyticsEvent(eventType: String, eventData: String = "") {
    val service = Retrofit.Builder()
            .client(OkHttpClient.Builder()
                    .callTimeout(10, TimeUnit.SECONDS)
                    .build())
            .baseUrl(HOST_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DiscordBotService::class.java)

    service.logAnalyticsEvent(AnalyticsRequest(ConfigPersistence.getId().orEmpty(), eventType, eventData)).enqueue(object : Callback<Void> {

        override fun onResponse(call: Call<Void>, response: Response<Void>) {
            if (response.isSuccessful) {
                println("Analytics sent! eventType='$eventType', eventData='$eventData'")
            } else {
                println("Analytics failed! code=${response.code()}")
            }
        }

        override fun onFailure(call: Call<Void>, t: Throwable) {
            println("Analytics failed! exception=$t")
        }
    })
}

private interface DiscordBotService {

    @POST("/createId")
    suspend fun createId(): Response<CreateIdResponse>

    @POST("/")
    fun playSound(@Body request: PlaySoundRequest): Call<Void>

    @POST("/analytics/logEvent")
    fun logAnalyticsEvent(@Body request: AnalyticsRequest): Call<Void>
}

data class CreateIdResponse(val userId: String)

data class AnalyticsRequest(val userId: String, val eventType: String, val eventData: String)

data class PlaySoundRequest(val userId: String, val token: String, val soundFileName: String)
