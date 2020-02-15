package com.github.mrbean355.admiralbulldog.arch

import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Url
import java.util.concurrent.TimeUnit

interface NuulsService {

    @GET
    suspend fun get(@Url url: String): Response<ResponseBody>

    companion object {
        val INSTANCE: NuulsService = Retrofit.Builder()
                .client(OkHttpClient.Builder()
                        .callTimeout(10, TimeUnit.SECONDS)
                        .build())
                .baseUrl("http://unused/")
                .build()
                .create()
    }
}