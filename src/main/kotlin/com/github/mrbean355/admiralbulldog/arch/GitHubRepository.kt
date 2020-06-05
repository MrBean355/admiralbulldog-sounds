package com.github.mrbean355.admiralbulldog.arch

import okhttp3.ResponseBody
import org.slf4j.LoggerFactory

class GitHubRepository {
    private val logger = LoggerFactory.getLogger(GitHubRepository::class.java)

    suspend fun getLatestAppRelease(): ServiceResponse<ReleaseInfo> {
        return try {
            GitHubService.INSTANCE.getLatestReleaseInfo(GitHubService.REPO_APP).toServiceResponse()
        } catch (t: Throwable) {
            logger.error("Failed to get latest app release", t)
            ServiceResponse.Exception()
        }

    }

    suspend fun getLatestModRelease(): ServiceResponse<ReleaseInfo> {
        return try {
            GitHubService.INSTANCE.getLatestReleaseInfo(GitHubService.REPO_MOD).toServiceResponse()
        } catch (t: Throwable) {
            logger.error("Failed to get latest mod release", t)
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
