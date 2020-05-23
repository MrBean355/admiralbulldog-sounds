package com.github.mrbean355.admiralbulldog.home

import com.github.mrbean355.admiralbulldog.APP_VERSION
import com.github.mrbean355.admiralbulldog.arch.AppViewModel
import com.github.mrbean355.admiralbulldog.arch.DiscordBotRepository
import com.github.mrbean355.admiralbulldog.arch.logAnalyticsEvent
import com.github.mrbean355.admiralbulldog.common.URL_DISCORD_SERVER_INVITE
import com.github.mrbean355.admiralbulldog.common.URL_PROJECT_WEBSITE
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.common.information
import com.github.mrbean355.admiralbulldog.discord.DiscordBotScreen
import com.github.mrbean355.admiralbulldog.game.monitorGameStateUpdates
import com.github.mrbean355.admiralbulldog.installation.InstallationWizard
import com.github.mrbean355.admiralbulldog.mod.DotaModScreen
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import com.github.mrbean355.admiralbulldog.persistence.DotaMod
import com.github.mrbean355.admiralbulldog.persistence.DotaPath
import com.github.mrbean355.admiralbulldog.persistence.GameStateIntegration
import com.github.mrbean355.admiralbulldog.settings.UpdateViewModel
import com.github.mrbean355.admiralbulldog.sounds.SyncSoundBitesScreen
import com.github.mrbean355.admiralbulldog.sounds.ToggleSoundEventsScreen
import javafx.beans.binding.BooleanBinding
import javafx.beans.binding.StringBinding
import javafx.beans.property.StringProperty
import javafx.scene.control.ButtonType
import kotlinx.coroutines.launch
import tornadofx.Scope
import tornadofx.booleanProperty
import tornadofx.error
import tornadofx.find
import tornadofx.runLater
import tornadofx.stringBinding
import tornadofx.stringProperty
import kotlin.concurrent.timer
import kotlin.system.exitProcess

private const val HEARTBEAT_FREQUENCY_MS = 30 * 1_000L

class MainViewModel : AppViewModel() {
    private val discordBotRepository = DiscordBotRepository()
    private val updateViewModel by inject<UpdateViewModel>()
    private val hasHeardFromDota = booleanProperty(false)

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

        checkForNewSounds()
        checkForAppUpdate()

        monitorGameStateUpdates {
            runLater {
                hasHeardFromDota.set(true)
            }
        }
    }

    override fun onUndock() {
        updateViewModel.onUndock()
        super.onUndock()
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

    private fun checkForNewSounds() {
        if (updateViewModel.shouldCheckForNewSounds()) {
            find<SyncSoundBitesScreen>().openModal(escapeClosesWindow = false, block = true, resizable = false)
        }
    }

    private fun checkForAppUpdate() {
        if (!updateViewModel.shouldCheckForAppUpdate()) {
            checkForModUpdate()
            return
        }
        coroutineScope.launch {
            updateViewModel.checkForAppUpdate(
                    onError = { checkForModUpdate() },
                    onUpdateSkipped = { checkForModUpdate() },
                    onNoUpdate = { checkForModUpdate() }
            )
        }
    }

    private fun checkForModUpdate() {
        if (ConfigPersistence.isModEnabled()) {
            DotaMod.onModEnabled()
            if (updateViewModel.shouldCheckForModUpdate()) {
                coroutineScope.launch {
                    updateViewModel.checkForModUpdate()
                }
            }
        } else {
            DotaMod.onModDisabled()
        }
    }

    private fun sendHeartbeats() {
        timer(daemon = true, period = HEARTBEAT_FREQUENCY_MS) {
            coroutineScope.launch {
                discordBotRepository.sendHeartbeat()
            }
        }
    }
}