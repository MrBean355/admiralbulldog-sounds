package com.github.mrbean355.admiralbulldog.arch.repo

import com.github.mrbean355.admiralbulldog.arch.AssetInfo
import com.github.mrbean355.admiralbulldog.arch.ReleaseInfo
import com.github.mrbean355.admiralbulldog.arch.ServiceResponse
import com.github.mrbean355.admiralbulldog.arch.callService
import com.github.mrbean355.admiralbulldog.arch.service.GitHubService
import com.github.mrbean355.admiralbulldog.arch.toServiceResponse
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
