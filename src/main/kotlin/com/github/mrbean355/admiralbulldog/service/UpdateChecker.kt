package com.github.mrbean355.admiralbulldog.service

import org.slf4j.LoggerFactory

class UpdateChecker {
    private val logger = LoggerFactory.getLogger(UpdateChecker::class.java)

    /** @return the info for the latest release, pulled from the GitHub releases page. */
    suspend fun getLatestReleaseInfo(): ReleaseInfo? {
        val response = GitHubService.instance.getLatestReleaseInfo()
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
}