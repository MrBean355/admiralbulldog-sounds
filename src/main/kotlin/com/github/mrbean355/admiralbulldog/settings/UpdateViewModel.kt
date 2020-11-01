package com.github.mrbean355.admiralbulldog.settings

import com.github.mrbean355.admiralbulldog.APP_VERSION
import com.github.mrbean355.admiralbulldog.arch.AppViewModel
import com.github.mrbean355.admiralbulldog.arch.DotaMod
import com.github.mrbean355.admiralbulldog.arch.ReleaseInfo
import com.github.mrbean355.admiralbulldog.arch.getAppAssetInfo
import com.github.mrbean355.admiralbulldog.arch.repo.DotaModRepository
import com.github.mrbean355.admiralbulldog.arch.repo.GitHubRepository
import com.github.mrbean355.admiralbulldog.common.*
import com.github.mrbean355.admiralbulldog.common.update.DownloadUpdateScreen
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import com.github.mrbean355.admiralbulldog.ui.showProgressScreen
import com.vdurmont.semver4j.Semver
import javafx.scene.control.ButtonType
import kotlinx.coroutines.launch
import java.io.File
import kotlin.system.exitProcess

class UpdateViewModel : AppViewModel() {
    private val gitHubRepository = GitHubRepository()
    private val dotaModRepository = DotaModRepository()

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
    fun checkForAppUpdate(onError: () -> Unit = {}, onUpdateSkipped: () -> Unit = {}, onNoUpdate: () -> Unit = {}) {
        viewModelScope.launch {
            val resource = gitHubRepository.getLatestAppRelease()
            val releaseInfo = resource.body
            if (!resource.isSuccessful() || releaseInfo == null) {
                logger.error("Failed to check for app update")
                onError()
                return@launch
            }
            val latestVersion = Semver(releaseInfo.tagName.removeVersionPrefix())
            if (latestVersion > APP_VERSION) {
                logger.info("New app version available: $releaseInfo")
                if (doesUserWantToUpdate(getString("header_app_update_available"), releaseInfo)) {
                    downloadAppUpdate(releaseInfo)
                } else {
                    onUpdateSkipped()
                }
            } else {
                logger.info("Already on latest app version")
                ConfigPersistence.setAppLastUpdateToNow()
                onNoUpdate()
            }
        }
    }

    fun checkForModUpdates(onError: () -> Unit = {}, onNoUpdate: () -> Unit = {}) {
        viewModelScope.launch {
            val response = dotaModRepository.checkForUpdates()
            val updates = response.body
            if (!response.isSuccessful() || updates == null) {
                logger.error("Failed to check for mod updates")
                onError()
                return@launch
            }
            if (updates.isNotEmpty()) {
                showInformation(
                        header = getString("header_mod_updates_available"),
                        content = getString("content_mod_updates_available", updates.joinToString { it.name }),
                        buttons = arrayOf(UPDATE_BUTTON, ButtonType.CANCEL)
                ) {
                    if (it === UPDATE_BUTTON) {
                        downloadModUpdates(updates)
                    }
                }
            } else {
                onNoUpdate()
            }
        }
    }

    private fun doesUserWantToUpdate(header: String, releaseInfo: ReleaseInfo): Boolean {
        var action: ButtonType? = null
        showInformation(
                header = header,
                content = getString("msg_update_available", releaseInfo.name, releaseInfo.publishedAt),
                buttons = arrayOf(WHATS_NEW_BUTTON, UPDATE_BUTTON, ButtonType.CANCEL)
        ) {
            action = it
        }
        if (action === WHATS_NEW_BUTTON) {
            hostServices.showDocument(releaseInfo.htmlUrl)
            return doesUserWantToUpdate(header, releaseInfo)
        }
        return action === UPDATE_BUTTON
    }

    private fun downloadAppUpdate(releaseInfo: ReleaseInfo) {
        val assetInfo = releaseInfo.getAppAssetInfo() ?: return

        find<DownloadUpdateScreen>(DownloadUpdateScreen.params(assetInfo, destination = "."))
                .openModal(escapeClosesWindow = false, resizable = false)

        subscribe<DownloadUpdateScreen.SuccessEvent>(times = 1) {
            ConfigPersistence.setAppLastUpdateToNow()
            showInformation(
                    header = getString("header_app_update_downloaded"),
                    content = getString("msg_app_update_downloaded", File(assetInfo.name).absolutePath),
                    buttons = arrayOf(ButtonType.FINISH)
            )
            exitProcess(0)
        }
    }

    private suspend fun downloadModUpdates(mods: Collection<DotaMod>) {
        val progressScreen = showProgressScreen()
        val allSucceeded = dotaModRepository.updateMods(mods)
        progressScreen.close()
        if (allSucceeded) {
            showInformation(getString("header_mod_updates_succeeded"), getString("content_mod_updates_succeeded"))
        } else {
            showWarning(getString("header_mod_updates_failed"), getString("content_mod_updates_failed"), RETRY_BUTTON, ButtonType.CANCEL) {
                if (it === RETRY_BUTTON) {
                    downloadModUpdates(mods)
                }
            }
        }
    }
}