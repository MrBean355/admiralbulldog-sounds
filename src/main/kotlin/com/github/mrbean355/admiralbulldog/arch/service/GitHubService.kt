package com.github.mrbean355.admiralbulldog.arch.service

import com.github.mrbean355.admiralbulldog.arch.ReleaseInfo
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Streaming
import retrofit2.http.Url

interface GitHubService {

    @GET("https://api.github.com/repos/MrBean355/{repo}/releases/latest")
    suspend fun getLatestReleaseInfo(@Path("repo") repo: String): Response<ReleaseInfo>

    @GET
    @Streaming
    suspend fun downloadLatestRelease(@Url url: String): Response<ResponseBody>

    companion object {
        const val REPO_APP = "admiralbulldog-sounds"
        const val REPO_MOD = "admiralbulldog-mod"
        val INSTANCE: GitHubService = Retrofit.Builder()
                .baseUrl("http://unused")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create()
    }
}
