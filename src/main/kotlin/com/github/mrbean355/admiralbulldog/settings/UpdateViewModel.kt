package com.github.mrbean355.admiralbulldog.settings

import com.github.mrbean355.admiralbulldog.APP_VERSION
import com.github.mrbean355.admiralbulldog.arch.AppViewModel
import com.github.mrbean355.admiralbulldog.arch.ReleaseInfo
import com.github.mrbean355.admiralbulldog.arch.getAppAssetInfo
import com.github.mrbean355.admiralbulldog.arch.getModAssetInfo
import com.github.mrbean355.admiralbulldog.arch.repo.GitHubRepository
import com.github.mrbean355.admiralbulldog.common.*
import com.github.mrbean355.admiralbulldog.common.update.DownloadUpdateScreen
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import com.github.mrbean355.admiralbulldog.persistence.DotaMod
import com.github.mrbean355.admiralbulldog.persistence.DotaPath
import com.vdurmont.semver4j.Semver
import javafx.scene.control.ButtonType
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.system.exitProcess

class UpdateViewModel : AppViewModel() {
    private val gitHubRepository = GitHubRepository()

    fun shouldCheckForAppUpdate(): Boolean {
        return ConfigPersistence.getAppUpdateFrequency().lessThanTimeSince(ConfigPersistence.getAppLastUpdateAt())
    }

    fun shouldCheckForNewSounds(): Boolean {
        return ConfigPersistence.getSoundsUpdateFrequency().lessThanTimeSince(ConfigPersistence.getSoundsLastUpdateAt())
    }

    fun shouldCheckForModUpdate(): Boolean {
        return ConfigPersistence.getModUpdateFrequency().lessThanTimeSince(ConfigPersistence.getModLastUpdateAt())
    }

    /**
     * Check if there's an app update available, prompting the user to download if there is.
     * @param onError         called if the check fails.
     * @param onUpdateSkipped invoked if the user chooses not to download the update.
     * @param onNoUpdate      called if there's no update available, or the users skips the download.
     */
    suspend fun checkForAppUpdate(onError: () -> Unit = {}, onUpdateSkipped: () -> Unit = {}, onNoUpdate: () -> Unit = {}) {
        val resource = gitHubRepository.getLatestAppRelease()
        val releaseInfo = resource.body
        if (!resource.isSuccessful() || releaseInfo == null) {
            withContext(Main) {
                logger.error("Failed to check for app update")
                onError()
            }
            return
        }
        val latestVersion = Semver(releaseInfo.tagName.removeVersionPrefix())
        if (latestVersion > APP_VERSION) {
            logger.info("New app version available: $releaseInfo")
            withContext(Main) {
                if (doesUserWantToUpdate(getString("header_app_update_available"), releaseInfo)) {
                    downloadAppUpdate(releaseInfo)
                } else {
                    onUpdateSkipped()
                }
            }
        } else {
            logger.info("Already on latest app version")
            ConfigPersistence.setAppLastUpdateToNow()
            withContext(Main) {
                onNoUpdate()
            }
        }
    }

    /**
     * Check if there's a mod update available, prompting the user to download if there is.
     * @param onError         called if the check fails.
     * @param onUpdateSkipped invoked if the user chooses not to download the update.
     * @param onNoUpdate      called if there's no update available, or the users skips the download.
     */
    suspend fun checkForModUpdate(onError: () -> Unit = {}, onUpdateSkipped: () -> Unit = {}, onNoUpdate: () -> Unit = {}) {
        val resource = gitHubRepository.getLatestModRelease()
        val releaseInfo = resource.body
        if (!resource.isSuccessful() || releaseInfo == null) {
            withContext(Main) {
                logger.error("Failed to check for mod update")
                onError()
            }
            return
        }
        if (DotaMod.shouldDownloadUpdate(releaseInfo)) {
            logger.info("New mod version available: $releaseInfo")
            withContext(Main) {
                if (doesUserWantToUpdate(getString("header_mod_update_available"), releaseInfo)) {
                    downloadModUpdate(releaseInfo)
                } else {
                    onUpdateSkipped()
                }
            }
        } else {
            logger.info("Already on latest mod version")
            ConfigPersistence.setModLastUpdateToNow()
            withContext(Main) {
                onNoUpdate()
            }
        }
    }

    private fun doesUserWantToUpdate(header: String, releaseInfo: ReleaseInfo): Boolean {
        var action: ButtonType? = null
        information(
                header = header,
                content = getString("msg_update_available", releaseInfo.name, releaseInfo.publishedAt),
                buttons = arrayOf(WHATS_NEW_BUTTON, DOWNLOAD_BUTTON, ButtonType.CANCEL)
        ) {
            action = it
        }
        if (action === WHATS_NEW_BUTTON) {
            hostServices.showDocument(releaseInfo.htmlUrl)
            return doesUserWantToUpdate(header, releaseInfo)
        }
        return action === DOWNLOAD_BUTTON
    }

    private fun downloadAppUpdate(releaseInfo: ReleaseInfo) {
        val assetInfo = releaseInfo.getAppAssetInfo() ?: return

        find<DownloadUpdateScreen>(DownloadUpdateScreen.params(assetInfo, destination = "."))
                .openModal(escapeClosesWindow = false, resizable = false)

        subscribe<DownloadUpdateScreen.SuccessEvent>(times = 1) {
            ConfigPersistence.setAppLastUpdateToNow()
            information(
                    header = getString("header_app_update_downloaded"),
                    content = getString("msg_app_update_downloaded", File(assetInfo.name).absolutePath),
                    buttons = arrayOf(ButtonType.FINISH)
            )
            exitProcess(0)
        }
    }

    private fun downloadModUpdate(releaseInfo: ReleaseInfo) {
        val assetInfo = releaseInfo.getModAssetInfo() ?: return

        find<DownloadUpdateScreen>(DownloadUpdateScreen.params(assetInfo, destination = DotaPath.getModDirectory()))
                .openModal(escapeClosesWindow = false, resizable = false)

        subscribe<DownloadUpdateScreen.SuccessEvent>(times = 1) {
            DotaMod.onModDownloaded(releaseInfo)
            ConfigPersistence.setModLastUpdateToNow()
            information(
                    header = getString("header_mod_update_downloaded"),
                    content = getString("content_mod_installed"),
                    buttons = arrayOf(ButtonType.FINISH)
            )
        }
    }
}