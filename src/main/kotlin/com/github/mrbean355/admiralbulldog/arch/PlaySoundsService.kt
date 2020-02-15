package com.github.mrbean355.admiralbulldog.arch

import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.create
import retrofit2.http.GET
import java.util.concurrent.TimeUnit

interface PlaySoundsService {

    @GET("playsounds")
    suspend fun getHtml(): Response<ResponseBody>

    companion object {
        val INSTANCE: PlaySoundsService = Retrofit.Builder()
                .client(OkHttpClient.Builder()
                        .callTimeout(10, TimeUnit.SECONDS)
                        .build())
                .baseUrl("http://chatbot.admiralbulldog.live/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .build()
                .create()
    }
}