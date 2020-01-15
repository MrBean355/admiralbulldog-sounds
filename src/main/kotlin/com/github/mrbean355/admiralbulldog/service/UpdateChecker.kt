package com.github.mrbean355.admiralbulldog.service

import org.slf4j.LoggerFactory

object UpdateChecker {
    private val logger = LoggerFactory.getLogger(UpdateChecker::class.java)

    /** @return the info for the latest release, pulled from the GitHub releases page. */
    suspend fun getLatestAppReleaseInfo(): ReleaseInfo? {
        val response = GitHubService.instance.getLatestAppReleaseInfo()
        val body = response.body()
        if (!response.isSuccessful || body == null) {
            logger.error("New version check failed: $response")
            return null
        }
        val jarAssetInfo = body.getJarAssetInfo()
        if (jarAssetInfo == null) {
            logger.error("Couldn't find JAR asset info")
            return null
        }
        return body
    }

    /** @return the info for the latest mod release, pulled from the GitHub releases page. */
    suspend fun getLatestModReleaseInfo(): ReleaseInfo? {
        val response = GitHubService.instance.getLatestModReleaseInfo()
        val body = response.body()
        if (!response.isSuccessful || body == null) {
            logger.error("New mod version check failed: $response")
            return null
        }
        val vpkAssetInfo = body.getModAssetInfo()
        if (vpkAssetInfo == null) {
            logger.error("Couldn't find VPK asset info")
            return null
        }
        return body
    }
}