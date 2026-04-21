package com.github.mrbean355.admiralbulldog.settings

import com.github.mrbean355.admiralbulldog.APP_VERSION
import com.github.mrbean355.admiralbulldog.arch.ComposeViewModel
import com.github.mrbean355.admiralbulldog.arch.DotaMod
import com.github.mrbean355.admiralbulldog.arch.ReleaseInfo
import com.github.mrbean355.admiralbulldog.arch.getAppAssetInfo
import com.github.mrbean355.admiralbulldog.arch.repo.DotaModRepository
import com.github.mrbean355.admiralbulldog.arch.repo.GitHubRepository
import com.github.mrbean355.admiralbulldog.common.AlertButton
import com.github.mrbean355.admiralbulldog.common.browseUrl
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.common.logger
import com.github.mrbean355.admiralbulldog.common.removeVersionPrefix
import com.github.mrbean355.admiralbulldog.common.showInformation
import com.github.mrbean355.admiralbulldog.common.showWarning
import com.github.mrbean355.admiralbulldog.common.update.openDownloadUpdateScreen
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import com.github.mrbean355.admiralbulldog.ui.showProgressScreen
import com.vdurmont.semver4j.Semver
import kotlinx.coroutines.launch
import java.io.File
import kotlin.system.exitProcess

class UpdateViewModel : ComposeViewModel() {
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
                promptForAppUpdate(getString("header_app_update_available"), releaseInfo, onUpdateSkipped)
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
                    buttons = arrayOf(AlertButton.UPDATE, AlertButton.CANCEL)
                ) {
                    if (it == AlertButton.UPDATE) {
                        viewModelScope.launch {
                            downloadModUpdates(updates)
                        }
                    }
                }
            } else {
                onNoUpdate()
            }
        }
    }

    private fun promptForAppUpdate(header: String, releaseInfo: ReleaseInfo, onUpdateSkipped: () -> Unit) {
        showInformation(
            header = header,
            content = getString("msg_update_available", releaseInfo.name, releaseInfo.publishedAt),
            buttons = arrayOf(AlertButton.WHATS_NEW, AlertButton.UPDATE, AlertButton.CANCEL)
        ) { action ->
            when (action) {
                AlertButton.WHATS_NEW -> {
                    browseUrl(releaseInfo.htmlUrl)
                    promptForAppUpdate(header, releaseInfo, onUpdateSkipped)
                }

                AlertButton.UPDATE -> {
                    downloadAppUpdate(releaseInfo)
                }

                else -> {
                    onUpdateSkipped()
                }
            }
        }
    }

    private fun downloadAppUpdate(releaseInfo: ReleaseInfo) {
        val assetInfo = releaseInfo.getAppAssetInfo() ?: return

        openDownloadUpdateScreen(
            assetInfo = assetInfo,
            destination = ".",
            onSuccess = {
                ConfigPersistence.setAppLastUpdateToNow()
                showInformation(
                    header = getString("header_app_update_downloaded"),
                    content = getString("msg_app_update_downloaded", File(assetInfo.name).absolutePath),
                    buttons = arrayOf(AlertButton.FINISH)
                ) {
                    exitProcess(0)
                }
            }
        )
    }

    private suspend fun downloadModUpdates(mods: Collection<DotaMod>) {
        val progressDialog = showProgressScreen()
        val allSucceeded = dotaModRepository.updateMods(mods)
        progressDialog.dispose()
        if (allSucceeded) {
            showInformation(getString("header_mod_updates_succeeded"), getString("content_mod_updates_succeeded"))
        } else {
            showWarning(getString("header_mod_updates_failed"), getString("content_mod_updates_failed"), AlertButton.RETRY, AlertButton.CANCEL) {
                if (it == AlertButton.RETRY) {
                    viewModelScope.launch {
                        downloadModUpdates(mods)
                    }
                }
            }
        }
    }
}