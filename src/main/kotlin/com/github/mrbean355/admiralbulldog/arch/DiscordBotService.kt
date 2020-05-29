package com.github.mrbean355.admiralbulldog.arch

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Streaming

interface DiscordBotService {

    @POST("/createId")
    suspend fun createId(): Response<CreateIdResponse>

    @POST("/heartbeat")
    suspend fun heartbeat(@Query("userId") userId: String): Response<Void>

    @GET("/soundBites/listV2")
    suspend fun listSoundBites(): Response<Map<String, String>>

    @GET("/soundBites/{name}")
    @Streaming
    suspend fun downloadSoundBite(@Path("name") name: String): Response<ResponseBody>

    @GET("/lookupToken")
    suspend fun lookUpToken(@Query("token") token: String): Response<ResponseBody>

    @POST("/")
    suspend fun playSound(@Body request: PlaySoundRequest): Response<Void>

    @POST("/analytics/logEvent")
    suspend fun logAnalyticsEvent(@Body request: AnalyticsRequest): Response<Void>

    @GET("/mods")
    suspend fun listMods(): Response<List<DotaMod>>

    companion object {
        val INSTANCE: DiscordBotService = Retrofit.Builder()
                .baseUrl(hostUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create()
    }
}
