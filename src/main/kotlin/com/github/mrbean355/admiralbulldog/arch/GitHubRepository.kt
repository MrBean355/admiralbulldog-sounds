package com.github.mrbean355.admiralbulldog.arch

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GitHubRepository {
    private val service = Retrofit.Builder()
            .baseUrl("http://unused")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GitHubService::class.java)

    suspend fun getLatestAppRelease(): Response<ReleaseInfo> {
        return service.getLatestReleaseInfo(GitHubService.REPO_APP)
    }

    suspend fun getLatestModRelease(): Response<ReleaseInfo> {
        return service.getLatestReleaseInfo(GitHubService.REPO_MOD)
    }

    suspend fun downloadAsset(assetInfo: AssetInfo): Response<ResponseBody> {
        return service.downloadLatestRelease(assetInfo.downloadUrl)
    }
}
