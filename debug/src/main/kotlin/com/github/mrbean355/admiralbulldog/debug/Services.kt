package com.github.mrbean355.admiralbulldog.debug

import javafx.scene.control.Label
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.util.Date
import java.util.concurrent.TimeUnit

val awsService: DiscordBotService = Retrofit.Builder()
        .client(OkHttpClient.Builder()
                .callTimeout(10, TimeUnit.SECONDS)
                .build())
        .baseUrl("http://prod.upmccxmkjx.us-east-2.elasticbeanstalk.com:8090")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(DiscordBotService::class.java)

val playSoundsService: PlaySoundsService = Retrofit.Builder()
        .client(OkHttpClient.Builder()
                .callTimeout(10, TimeUnit.SECONDS)
                .build())
        .baseUrl("http://chatbot.admiralbulldog.live/")
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()
        .create(PlaySoundsService::class.java)

val nuulsService: NuulsService = Retrofit.Builder()
        .client(OkHttpClient.Builder()
                .callTimeout(10, TimeUnit.SECONDS)
                .build())
        .baseUrl("https://i.nuuls.com/")
        .build()
        .create(NuulsService::class.java)

val gitHubService: GitHubService = Retrofit.Builder()
        .client(OkHttpClient.Builder()
                .callTimeout(10, TimeUnit.SECONDS)
                .build())
        .baseUrl("https://api.github.com/")
        .build()
        .create(GitHubService::class.java)

fun Label.testService(message: String, service: suspend () -> Response<*>) {
    text = message
    GlobalScope.launch {
        val start = System.currentTimeMillis()
        val result = runCatching { service() }
        val duration = System.currentTimeMillis() - start
        withContext(Dispatchers.Main) {
            result.onSuccess {
                text = "$message done in ${duration}ms! Status code: ${it.code()}"
            }
            result.onFailure {
                text = "$message failed in ${duration}ms! See the 'debug_crash_log.txt' file."
                logToFile(message, it)
            }
        }
    }
}

private fun logToFile(message: String, t: Throwable) {
    val stringWriter = StringWriter()
    t.printStackTrace(PrintWriter(stringWriter))
    val stackTrace = stringWriter.toString()
    val file = File("debug_crash_log.txt")
    file.appendText("[${Date()}] $message\n")
    file.appendText(stackTrace)
    file.appendText("\n\n")
}

interface DiscordBotService {

    @GET("/metadata/laterVersion")
    suspend fun hasLaterVersion(@Query("version") version: String): Response<Boolean>
}

interface PlaySoundsService {

    @GET("playsounds")
    suspend fun getHtml(): Response<ResponseBody>
}

interface NuulsService {
    @GET("{name}")
    suspend fun get(@Path("name") name: String): Response<ResponseBody>
}

interface GitHubService {

    @GET("repos/MrBean355/admiralbulldog-sounds/releases/latest")
    suspend fun getLatestReleaseInfo(): Response<ResponseBody>
}
