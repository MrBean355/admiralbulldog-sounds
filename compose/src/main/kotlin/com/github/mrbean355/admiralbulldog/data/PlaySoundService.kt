/*
 * Copyright 2021 Michael Johnston
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

package com.github.mrbean355.admiralbulldog.data

import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Streaming

interface PlaySoundService {

    @GET("/soundBites/listV2")
    suspend fun listSoundBites(): Map<String, String>

    @GET("/soundBites/{name}")
    @Streaming
    suspend fun downloadSoundBite(@Path("name") name: String): ResponseBody

    companion object {
        val Instance: PlaySoundService = Retrofit.Builder()
            .baseUrl("http://prod.upmccxmkjx.us-east-2.elasticbeanstalk.com:8090/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create()
    }
}