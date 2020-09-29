package com.github.mrbean355.admiralbulldog.arch.repo

import com.github.mrbean355.admiralbulldog.arch.AssetInfo
import com.github.mrbean355.admiralbulldog.arch.ReleaseInfo
import com.github.mrbean355.admiralbulldog.arch.ServiceResponse
import com.github.mrbean355.admiralbulldog.arch.service.GitHubService
import com.github.mrbean355.admiralbulldog.arch.toServiceResponse
import okhttp3.ResponseBody
import org.slf4j.LoggerFactory

class GitHubRepository {
    private val logger = LoggerFactory.getLogger(GitHubRepository::class.java)

    suspend fun getLatestAppRelease(): ServiceResponse<ReleaseInfo> {
        return try {
            GitHubService.INSTANCE.getLatestReleaseInfo().toServiceResponse()
        } catch (t: Throwable) {
            logger.error("Failed to get latest app release", t)
            ServiceResponse.Exception()
        }

    }

    suspend fun downloadAsset(assetInfo: AssetInfo): ServiceResponse<ResponseBody> {
        return try {
            GitHubService.INSTANCE.downloadLatestRelease(assetInfo.downloadUrl).toServiceResponse()
        } catch (t: Throwable) {
            logger.error("Failed to download asset: $assetInfo", t)
            ServiceResponse.Exception()
        }
    }
}
