package com.github.mrbean355.admiralbulldog.arch

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface NuulsService {

    @GET
    suspend fun get(@Url url: String): Response<ResponseBody>

}