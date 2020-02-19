package com.github.mrbean355.admiralbulldog.arch

import okhttp3.ResponseBody

class GitHubRepository {

    suspend fun getLatestAppRelease(): ServiceResponse<ReleaseInfo> {
        return callService { GitHubService.INSTANCE.getLatestReleaseInfo(GitHubService.REPO_APP) }
                .toServiceResponse()
    }

    suspend fun getLatestModRelease(): ServiceResponse<ReleaseInfo> {
        return callService { GitHubService.INSTANCE.getLatestReleaseInfo(GitHubService.REPO_MOD) }
                .toServiceResponse()
    }

    suspend fun downloadAsset(assetInfo: AssetInfo): ServiceResponse<ResponseBody> {
        return callService { GitHubService.INSTANCE.downloadLatestRelease(assetInfo.downloadUrl) }
                .toServiceResponse()
    }
}
