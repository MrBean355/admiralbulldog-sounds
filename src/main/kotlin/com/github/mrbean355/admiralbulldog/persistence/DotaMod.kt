package com.github.mrbean355.admiralbulldog.persistence

import com.github.mrbean355.admiralbulldog.service.GitHubService
import com.github.mrbean355.admiralbulldog.service.ReleaseInfo
import com.github.mrbean355.admiralbulldog.ui.DotaPath
import com.github.mrbean355.admiralbulldog.ui.removeVersionPrefix
import com.vdurmont.semver4j.Semver
import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.File

object DotaMod {
    private val logger = LoggerFactory.getLogger(DotaMod::class.java)

    /**
     * Determines whether the latest version of the mod should be downloaded?
     * Returns `true` if any of these criteria are met:
     * - VPK file does not exist
     * - Config stores an old mod version
     * - VPK file has different SHA-512 to the latest release
     */
    suspend fun shouldDownloadUpdate(releaseInfo: ReleaseInfo): Boolean {
        val vpk = File(DotaPath.getModFilePath())
        if (!vpk.exists()) {
            logger.info("No VPK found, download")
            return true
        }
        val currentVersion = Semver(ConfigPersistence.getModVersion())
        val latestVersion = Semver(releaseInfo.tagName.removeVersionPrefix())
        if (latestVersion > currentVersion) {
            logger.info("Newer version available, download")
            return true
        }
        val checksumAssetInfo = releaseInfo.getModChecksumAssetInfo()
        if (checksumAssetInfo == null) {
            logger.error("No checksum asset info, giving up")
            return false
        }
        val response = GitHubService.instance.downloadLatestRelease(checksumAssetInfo.downloadUrl)
        val body = response.body()
        if (!response.isSuccessful || body == null) {
            logger.error("Error getting checksum, giving up")
            return false
        }
        val checksum = body.byteStream().bufferedReader().use(BufferedReader::readText)
        return !Checksum.verify(vpk, checksum)
    }
}