package com.github.mrbean355.admiralbulldog.home

import com.github.mrbean355.admiralbulldog.APP_VERSION
import com.github.mrbean355.admiralbulldog.DiscordBotScreen
import com.github.mrbean355.admiralbulldog.DotaModScreen
import com.github.mrbean355.admiralbulldog.DownloadUpdateScreen
import com.github.mrbean355.admiralbulldog.SyncSoundBitesScreen
import com.github.mrbean355.admiralbulldog.ToggleSoundEventsScreen
import com.github.mrbean355.admiralbulldog.arch.AppViewModel
import com.github.mrbean355.admiralbulldog.arch.DiscordBotRepository
import com.github.mrbean355.admiralbulldog.arch.GitHubRepository
import com.github.mrbean355.admiralbulldog.arch.ReleaseInfo
import com.github.mrbean355.admiralbulldog.arch.getAppAssetInfo
import com.github.mrbean355.admiralbulldog.arch.getModAssetInfo
import com.github.mrbean355.admiralbulldog.arch.logAnalyticsEvent
import com.github.mrbean355.admiralbulldog.assets.SoundBites
import com.github.mrbean355.admiralbulldog.common.DOWNLOAD_BUTTON
import com.github.mrbean355.admiralbulldog.common.URL_DISCORD_SERVER_INVITE
import com.github.mrbean355.admiralbulldog.common.URL_PROJECT_WEBSITE
import com.github.mrbean355.admiralbulldog.common.WHATS_NEW_BUTTON
import com.github.mrbean355.admiralbulldog.common.information
import com.github.mrbean355.admiralbulldog.common.logger
import com.github.mrbean355.admiralbulldog.game.monitorGameStateUpdates
import com.github.mrbean355.admiralbulldog.installation.InstallationWizard
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import com.github.mrbean355.admiralbulldog.persistence.DotaMod
import com.github.mrbean355.admiralbulldog.ui.DotaPath
import com.github.mrbean355.admiralbulldog.ui.GameStateIntegration
import com.github.mrbean355.admiralbulldog.ui.getString
import com.github.mrbean355.admiralbulldog.ui.removeVersionPrefix
import com.vdurmont.semver4j.Semver
import javafx.beans.binding.BooleanBinding
import javafx.beans.binding.StringBinding
import javafx.beans.property.StringProperty
import javafx.scene.control.ButtonType
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tornadofx.Scope
import tornadofx.booleanProperty
import tornadofx.error
import tornadofx.find
import tornadofx.runLater
import tornadofx.stringBinding
import tornadofx.stringProperty
import java.io.File
import kotlin.concurrent.timer
import kotlin.system.exitProcess

private const val HEARTBEAT_FREQUENCY_MS = 30 * 1_000L

class MainViewModel : AppViewModel() {
    private val hasHeardFromDota = booleanProperty(false)
    private val discordBotRepository = DiscordBotRepository()
    private val gitHubRepository = GitHubRepository()

    val heading: StringBinding = hasHeardFromDota.stringBinding {
        if (it == true) getString("msg_connected") else getString("msg_not_connected")
    }
    val progressBarVisible: BooleanBinding = hasHeardFromDota.not()
    val infoMessage: StringBinding = hasHeardFromDota.stringBinding {
        if (it == true) getString("dsc_connected") else getString("dsc_not_connected")
    }
    val version: StringProperty = stringProperty(getString("lbl_app_version", APP_VERSION.value))

    override fun onReady() {
        logAnalyticsEvent(eventType = "app_start", eventData = APP_VERSION.value)
        sendHeartbeats()

        ensureValidDotaPath()
        ensureGsiInstalled()

        if (SoundBites.shouldSync()) {
            find<SyncSoundBitesScreen>().openModal(escapeClosesWindow = false, block = true, resizable = false)
        }

        coroutineScope.launch {
            checkForAppUpdate()
        }
        monitorGameStateUpdates {
            runLater {
                hasHeardFromDota.set(true)
            }
        }
    }

    private fun ensureValidDotaPath() {
        if (DotaPath.hasValidSavedPath()) {
            return
        }
        find<InstallationWizard>(scope = Scope()).openModal(block = true, resizable = false)
        if (!DotaPath.hasValidSavedPath()) {
            error(getString("install_header"), getString("msg_installer_fail"))
            exitProcess(-1)
        }
    }

    private fun ensureGsiInstalled() {
        val alreadyInstalled = GameStateIntegration.isInstalled()
        GameStateIntegration.install()
        if (!alreadyInstalled) {
            information(getString("install_header"), getString("msg_installer_success"), ButtonType.FINISH)
        }
    }

    fun onChangeSoundsClicked() {
        find<ToggleSoundEventsScreen>().openModal(resizable = false)
    }

    fun onDiscordBotClicked() {
        find<DiscordBotScreen>().openModal(resizable = false)
    }

    fun onDotaModClicked() {
        find<DotaModScreen>().openModal(resizable = false)
    }

    fun onDiscordCommunityClicked() {
        hostServices.showDocument(URL_DISCORD_SERVER_INVITE)
    }

    fun onProjectWebsiteClicked() {
        hostServices.showDocument(URL_PROJECT_WEBSITE)
    }

    private suspend fun checkForAppUpdate() {
        logger.info("Checking for app update...")
        val resource = gitHubRepository.getLatestAppRelease()
        val releaseInfo = resource.body
        if (!resource.isSuccessful() || releaseInfo == null) {
            logger.warn("Bad app release info response, giving up")
            checkForModUpdate()
            return
        }
        val latestVersion = Semver(releaseInfo.tagName.removeVersionPrefix())
        if (latestVersion > APP_VERSION) {
            logger.info("Later app version available: ${releaseInfo.tagName}")
            withContext(Main) {
                if (doesUserWantToUpdate(getString("header_app_update_available"), releaseInfo)) {
                    downloadAppUpdate(releaseInfo)
                } else {
                    withContext(coroutineScope.coroutineContext) {
                        checkForModUpdate()
                    }
                }
            }
        } else {
            logger.info("Already at latest app version: ${releaseInfo.tagName}")
            checkForModUpdate()
        }
    }

    private fun downloadAppUpdate(releaseInfo: ReleaseInfo) {
        val assetInfo = releaseInfo.getAppAssetInfo() ?: return
        subscribe<DownloadUpdateScreen.SuccessEvent>(times = 1) {
            information(
                    header = getString("header_app_update_downloaded"),
                    content = getString("msg_app_update_downloaded", File(assetInfo.name).absolutePath),
                    buttons = *arrayOf(ButtonType.FINISH)
            )
            exitProcess(0)
        }
        find<DownloadUpdateScreen>(DownloadUpdateScreen.params(assetInfo, destination = "."))
                .openModal(escapeClosesWindow = false, block = true, resizable = false)
    }

    private suspend fun checkForModUpdate() {
        if (!ConfigPersistence.isModEnabled()) {
            logger.info("Mod is disabled, skipping update")
            DotaMod.onModDisabled()
            return
        }
        logger.info("Checking for mod update...")
        DotaMod.onModEnabled()
        val resource = gitHubRepository.getLatestModRelease()
        val releaseInfo = resource.body
        if (!resource.isSuccessful() || releaseInfo == null) {
            logger.warn("Bad mod release info response, giving up")
            return
        }
        if (DotaMod.shouldDownloadUpdate(releaseInfo)) {
            logger.info("Later mod version available: ${releaseInfo.tagName}")
            withContext(Main) {
                if (doesUserWantToUpdate(getString("header_mod_update_available"), releaseInfo)) {
                    downloadModUpdate(releaseInfo)
                }
            }
        } else {
            logger.info("Already at latest mod version: ${releaseInfo.tagName}")
        }
    }

    private fun downloadModUpdate(releaseInfo: ReleaseInfo) {
        val assetInfo = releaseInfo.getModAssetInfo() ?: return
        subscribe<DownloadUpdateScreen.SuccessEvent>(times = 1) {
            information(
                    header = getString("header_mod_update_downloaded"),
                    content = getString("msg_mod_restart_dota"),
                    buttons = *arrayOf(ButtonType.FINISH)
            )
        }
        find<DownloadUpdateScreen>(DownloadUpdateScreen.params(assetInfo, destination = DotaPath.getModDirectory()))
                .openModal(escapeClosesWindow = false, block = true, resizable = false)
    }

    private fun doesUserWantToUpdate(header: String, releaseInfo: ReleaseInfo): Boolean {
        var action: ButtonType? = null
        information(
                header = header,
                content = getString("msg_update_available", releaseInfo.name, releaseInfo.publishedAt),
                buttons = *arrayOf(WHATS_NEW_BUTTON, DOWNLOAD_BUTTON, ButtonType.CANCEL)
        ) {
            action = it
        }
        if (action === WHATS_NEW_BUTTON) {
            hostServices.showDocument(releaseInfo.htmlUrl)
            return doesUserWantToUpdate(header, releaseInfo)
        }
        return action === DOWNLOAD_BUTTON
    }

    private fun sendHeartbeats() {
        timer(daemon = true, period = HEARTBEAT_FREQUENCY_MS) {
            coroutineScope.launch {
                discordBotRepository.sendHeartbeat()
            }
        }
    }
}