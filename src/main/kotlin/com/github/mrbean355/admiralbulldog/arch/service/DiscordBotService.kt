/*
 * Copyright 2022 Michael Johnston
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.mrbean355.admiralbulldog.arch.service

import com.github.mrbean355.admiralbulldog.arch.AnalyticsRequest
import com.github.mrbean355.admiralbulldog.arch.CreateIdResponse
import com.github.mrbean355.admiralbulldog.arch.DotaMod
import com.github.mrbean355.admiralbulldog.arch.FeedbackRequest
import com.github.mrbean355.admiralbulldog.arch.PlaySoundRequest
import com.github.mrbean355.admiralbulldog.arch.PlaySoundsRequest
import com.github.mrbean355.admiralbulldog.arch.repo.hostUrl
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
import retrofit2.http.Url

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

    @POST("/feedback")
    suspend fun sendFeedback(@Body request: FeedbackRequest): Response<Void>

    companion object {
        val INSTANCE: DiscordBotService = Retrofit.Builder()
            .baseUrl(hostUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create()
    }
}
