package com.github.mrbean355.admiralbulldog.persistence

import com.github.mrbean355.admiralbulldog.arch.ReleaseInfo
import com.github.mrbean355.admiralbulldog.arch.getModChecksumAssetInfo
import com.github.mrbean355.admiralbulldog.arch.repo.GitHubRepository
import com.github.mrbean355.admiralbulldog.arch.verifyChecksum
import com.github.mrbean355.admiralbulldog.common.removeVersionPrefix
import com.vdurmont.semver4j.Semver
import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.File

private val GAME_DOTA_PATTERN = Regex("^\\s*Game\\s*dota\\s*$")
private val GAME_BULLDOG_PATTERN = Regex("^\\s*Game\\s*admiralbulldog\\s*$")

object DotaMod {
    private val logger = LoggerFactory.getLogger(DotaMod::class.java)
    private val gitHubRepository = GitHubRepository()

    /**
     * Determines whether the latest version of the mod should be downloaded.
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
        val currentVersion = Semver(ConfigPersistence.getModVersion().ifBlank { "0.0.0" })
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
        val response = gitHubRepository.downloadAsset(checksumAssetInfo)
        val body = response.body
        if (!response.isSuccessful() || body == null) {
            logger.error("Error getting checksum, giving up")
            return false
        }
        val checksum = body.byteStream().bufferedReader().use(BufferedReader::readText)
        return !vpk.verifyChecksum(checksum)
    }

    /** Register the mod in Dota's game info file. */
    fun onModEnabled() {
        if (!ConfigPersistence.isModTempDisabled()) {
            updateGameInfoFile(install = true)
        } else {
            updateGameInfoFile(install = false)
        }
    }

    /** Update the mod's version in the app's config file. */
    fun onModDownloaded(releaseInfo: ReleaseInfo) {
        ConfigPersistence.setModVersion(releaseInfo.tagName.removeVersionPrefix())
        if (ConfigPersistence.isModTempDisabled()) {
            ConfigPersistence.setModTempDisabled(false)
            updateGameInfoFile(install = true)
        }
    }

    /**
     * Unregister the mod from Dota's game info file.
     * @return `true` if the mod was previously enabled; `false` otherwise.
     */
    fun onModDisabled(): Boolean {
        return updateGameInfoFile(install = false)
    }

    private fun updateGameInfoFile(install: Boolean): Boolean {
        val gameInfo = File(DotaPath.getGameInfoFilePath())
        val builder = StringBuilder()
        var bulldogFound = false
        gameInfo.readLines().forEach { line ->
            if (install) {
                if (line.matches(GAME_BULLDOG_PATTERN)) {
                    bulldogFound = true
                }
                if (!bulldogFound && line.matches(GAME_DOTA_PATTERN)) {
                    logger.info("Inserting mod into game info file")
                    builder.append("\t\t\tGame\t\t\t\tadmiralbulldog\n")
                }
                builder.append(line).append('\n')
            } else {
                if (!line.matches(GAME_BULLDOG_PATTERN)) {
                    builder.append(line).append('\n')
                } else {
                    bulldogFound = true
                }
            }
        }
        gameInfo.writeText(builder.toString())
        logger.info("Wrote game info file: ${gameInfo.absolutePath}")
        return bulldogFound
    }
}