package com.github.mrbean355.admiralbulldog.arch

import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GitHubRepository {
    private val service = Retrofit.Builder()
            .baseUrl("http://unused")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GitHubService::class.java)

    suspend fun getLatestAppRelease(): ServiceResponse<ReleaseInfo> {
        return service.getLatestReleaseInfo(GitHubService.REPO_APP)
                .toServiceResponse()
    }

    suspend fun getLatestModRelease(): ServiceResponse<ReleaseInfo> {
        return service.getLatestReleaseInfo(GitHubService.REPO_MOD)
                .toServiceResponse()
    }

    suspend fun downloadAsset(assetInfo: AssetInfo): ServiceResponse<ResponseBody> {
        return service.downloadLatestRelease(assetInfo.downloadUrl)
                .toServiceResponse()
    }
}
