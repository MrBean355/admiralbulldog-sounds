package com.github.mrbean355.admiralbulldog.arch

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET

interface PlaySoundsService {

    @GET("playsounds")
    suspend fun getHtml(): Response<ResponseBody>

}