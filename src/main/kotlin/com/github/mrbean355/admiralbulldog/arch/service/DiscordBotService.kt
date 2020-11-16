package com.github.mrbean355.admiralbulldog.arch.service

import com.github.mrbean355.admiralbulldog.arch.*
import com.github.mrbean355.admiralbulldog.arch.repo.hostUrl
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.*

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

    @POST("/playSounds")
    suspend fun playSounds(@Body request: PlaySoundsRequest): Response<Void>

    @POST("/analytics/logProperties")
    suspend fun logAnalyticsProperties(@Body request: AnalyticsRequest): Response<Void>

    @GET("/mods")
    suspend fun listMods(): Response<List<DotaMod>>

    @GET
    @Streaming
    suspend fun downloadFile(@Url url: String): Response<ResponseBody>

    companion object {
        val INSTANCE: DiscordBotService = Retrofit.Builder()
                .baseUrl(hostUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create()
    }
}
