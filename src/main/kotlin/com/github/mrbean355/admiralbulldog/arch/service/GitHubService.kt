/*
 * Copyright 2023 Michael Johnston
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.mrbean355.admiralbulldog.arch.service

import com.github.mrbean355.admiralbulldog.arch.ReleaseInfo
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url

interface GitHubService {

    @GET("https://api.github.com/repos/MrBean355/admiralbulldog-sounds/releases/latest")
    suspend fun getLatestReleaseInfo(): Response<ReleaseInfo>

    @GET
    @Streaming
    suspend fun downloadLatestRelease(@Url url: String): Response<ResponseBody>

    companion object {
        val INSTANCE: GitHubService = Retrofit.Builder()
            .baseUrl("http://unused")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create()
    }
}
