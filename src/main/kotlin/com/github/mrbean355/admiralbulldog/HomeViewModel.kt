package com.github.mrbean355.admiralbulldog

import com.github.mrbean355.admiralbulldog.arch.GitHubRepository
import com.github.mrbean355.admiralbulldog.arch.ReleaseInfo
import com.github.mrbean355.admiralbulldog.arch.getAppAssetInfo
import com.github.mrbean355.admiralbulldog.arch.getModAssetInfo
import com.github.mrbean355.admiralbulldog.assets.SoundBytes
import com.github.mrbean355.admiralbulldog.game.monitorGameStateUpdates
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import com.github.mrbean355.admiralbulldog.persistence.DotaMod
import com.github.mrbean355.admiralbulldog.service.logAnalyticsEvent
import com.github.mrbean355.admiralbulldog.ui.Alert
import com.github.mrbean355.admiralbulldog.ui.DotaPath
import com.github.mrbean355.admiralbulldog.ui.Installer
import com.github.mrbean355.admiralbulldog.ui.getString
import com.github.mrbean355.admiralbulldog.ui.removeVersionPrefix
import com.github.mrbean355.admiralbulldog.ui.showModal
import com.github.mrbean355.admiralbulldog.ui.toNullable
import com.vdurmont.semver4j.Semver
import javafx.application.HostServices
import javafx.application.Platform
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ObservableValue
import javafx.scene.control.Alert
import javafx.scene.control.ButtonBar
import javafx.scene.control.ButtonType
import javafx.stage.Stage
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import java.io.File
import java.util.concurrent.Callable
import kotlin.system.exitProcess

class HomeViewModel(private val stage: Stage, private val hostServices: HostServices) {
    private val logger = LoggerFactory.getLogger(HomeViewModel::class.java)
    private val hasHeardFromDota = SimpleBooleanProperty(false)
    private val gitHubRepository = GitHubRepository()

    val heading: ObservableValue<String> = Bindings.createStringBinding(Callable {
        if (hasHeardFromDota.get()) getString("msg_connected") else getString("msg_not_connected")
    }, hasHeardFromDota)
    val progressBarVisible: ObservableValue<Boolean> = hasHeardFromDota.not()
    val infoMessage: ObservableValue<String> = Bindings.createStringBinding(Callable {
        if (hasHeardFromDota.get()) getString("dsc_connected") else getString("dsc_not_connected")
    }, hasHeardFromDota)
    val version = SimpleStringProperty(getString("lbl_app_version", APP_VERSION.value))

    fun init() {
        if (SoundBytes.shouldSync()) {
            SyncSoundBytesStage().showModal(owner = stage, wait = true)
        }
        val dotaPath = loadDotaPath()
        Installer.installIfNecessary(dotaPath)
        checkForInvalidSounds()
        GlobalScope.launch {
            checkForAppUpdate()
        }
        stage.show()
        monitorGameStateUpdates {
            Platform.runLater {
                hasHeardFromDota.set(true)
            }
        }
        logAnalyticsEvent(eventType = "app_start", eventData = APP_VERSION.value)
    }

    fun onChangeSoundsClicked() {
        ToggleSoundEventsStage().showModal(owner = stage)
    }

    fun onDiscordBotClicked() {
        DiscordBotStage(hostServices).showModal(owner = stage)
    }

    fun onDotaModClicked() {
        logAnalyticsEvent(eventType = "button_click", eventData = "dota_mod")
        DotaModStage(hostServices).showModal(owner = stage)
    }

    fun onDiscordCommunityClicked() {
        logAnalyticsEvent(eventType = "button_click", eventData = "discord_community")
        hostServices.showDocument(URL_DISCORD_SERVER_INVITE)
    }

    fun onProjectWebsiteClicked() {
        logAnalyticsEvent(eventType = "button_click", eventData = "project_website")
        hostServices.showDocument(URL_PROJECT_WEBSITE)
    }

    private fun loadDotaPath(): String {
        val result = runCatching {
            DotaPath.loadPath(stage)
        }
        if (result.isSuccess) {
            return result.getOrThrow()
        }
        Alert(type = Alert.AlertType.ERROR,
                header = getString("header_installer"),
                content = getString("msg_no_dota_path"),
                buttons = arrayOf(ButtonType.CLOSE),
                owner = stage
        ).showAndWait()
        exitProcess(-1)
    }

    private fun checkForInvalidSounds() {
        val invalidSounds = ConfigPersistence.getInvalidSounds()
        if (invalidSounds.isNotEmpty()) {
            Alert(type = Alert.AlertType.WARNING,
                    header = getString("header_sounds_removed"),
                    content = getString("msg_sounds_removed", invalidSounds.joinToString(separator = "\n")),
                    buttons = arrayOf(ButtonType.OK),
                    owner = stage
            ).showAndWait()
            ConfigPersistence.clearInvalidSounds()
        }
    }

    private suspend fun checkForAppUpdate() {
        logger.info("Checking for app update...")
        val resource = gitHubRepository.getLatestAppRelease()
        val releaseInfo = resource.body()
        if (!resource.isSuccessful || releaseInfo == null) {
            logger.warn("Bad app release info response, giving up")
            checkForModUpdate()
            return
        }
        val latestVersion = Semver(releaseInfo.tagName.removeVersionPrefix())
        if (latestVersion > APP_VERSION) {
            logger.info("Later app version available: ${releaseInfo.tagName}")
            withContext(Main) {
                if (doesUserWantToUpdate(getString("header_app_update_available"), releaseInfo)) {
                    logAnalyticsEvent(eventType = "button_click", eventData = "download_app_update")
                    downloadAppUpdate(releaseInfo)
                } else {
                    withContext(Default) {
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
        DownloadUpdateStage(assetInfo, destination = ".") {
            Alert(type = Alert.AlertType.INFORMATION,
                    header = getString("header_app_update_downloaded"),
                    content = getString("msg_app_update_downloaded", File(assetInfo.name).absolutePath),
                    buttons = arrayOf(ButtonType.FINISH),
                    owner = stage
            ).showAndWait()
            exitProcess(0)
        }.showModal(stage)
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
        val releaseInfo = resource.body()
        if (!resource.isSuccessful || releaseInfo == null) {
            logger.warn("Bad mod release info response, giving up")
            return
        }
        if (DotaMod.shouldDownloadUpdate(releaseInfo)) {
            logger.info("Later mod version available: ${releaseInfo.tagName}")
            withContext(Main) {
                if (doesUserWantToUpdate(getString("header_mod_update_available"), releaseInfo)) {
                    logAnalyticsEvent(eventType = "button_click", eventData = "download_mod_update")
                    downloadModUpdate(releaseInfo)
                }
            }
        } else {
            logger.info("Already at latest mod version: ${releaseInfo.tagName}")
        }
    }

    private fun downloadModUpdate(releaseInfo: ReleaseInfo) {
        val assetInfo = releaseInfo.getModAssetInfo() ?: return
        DownloadUpdateStage(assetInfo, destination = DotaPath.getModDirectory()) {
            DotaMod.onModDownloaded(releaseInfo)
            Alert(type = Alert.AlertType.INFORMATION,
                    header = getString("header_mod_update_downloaded"),
                    content = getString("msg_mod_update_downloaded"),
                    buttons = arrayOf(ButtonType.FINISH),
                    owner = stage
            ).showAndWait()
        }.showModal(stage)
    }

    private fun doesUserWantToUpdate(header: String, releaseInfo: ReleaseInfo): Boolean {
        val whatsNewButton = ButtonType(getString("btn_whats_new"), ButtonBar.ButtonData.HELP_2)
        val downloadButton = ButtonType(getString("btn_download"), ButtonBar.ButtonData.NEXT_FORWARD)
        val action = Alert(
                type = Alert.AlertType.INFORMATION,
                header = header,
                content = getString("msg_update_available", releaseInfo.name, releaseInfo.publishedAt),
                buttons = arrayOf(whatsNewButton, downloadButton, ButtonType.CANCEL),
                owner = stage
        ).showAndWait().toNullable()

        if (action === whatsNewButton) {
            hostServices.showDocument(releaseInfo.htmlUrl)
            return doesUserWantToUpdate(header, releaseInfo)
        }
        return action === downloadButton
    }
}