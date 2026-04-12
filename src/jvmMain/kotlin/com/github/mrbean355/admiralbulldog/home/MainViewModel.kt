package com.github.mrbean355.admiralbulldog.home

import com.github.mrbean355.admiralbulldog.APP_VERSION
import com.github.mrbean355.admiralbulldog.arch.ComposeViewModel
import com.github.mrbean355.admiralbulldog.arch.logAnalyticsProperties
import com.github.mrbean355.admiralbulldog.arch.repo.DiscordBotRepository
import com.github.mrbean355.admiralbulldog.arch.repo.DotaModRepository
import com.github.mrbean355.admiralbulldog.assets.SoundBites
import com.github.mrbean355.admiralbulldog.common.getDistributionName
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.common.showError
import com.github.mrbean355.admiralbulldog.discord.openDiscordBotScreen
import com.github.mrbean355.admiralbulldog.feedback.FeedbackScreen
import com.github.mrbean355.admiralbulldog.feedback.openFeedbackScreen
import com.github.mrbean355.admiralbulldog.game.monitorGameStateUpdates
import com.github.mrbean355.admiralbulldog.game.openRoshanTimerScreen
import com.github.mrbean355.admiralbulldog.installation.openInstallationWizard
import com.github.mrbean355.admiralbulldog.mods.OldModMigration
import com.github.mrbean355.admiralbulldog.mods.openDotaModsScreen
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import com.github.mrbean355.admiralbulldog.persistence.DotaPath
import com.github.mrbean355.admiralbulldog.persistence.GameStateIntegration
import com.github.mrbean355.admiralbulldog.settings.UpdateViewModel
import com.github.mrbean355.admiralbulldog.settings.openSettingsScreen
import com.github.mrbean355.admiralbulldog.sounds.openViewSoundTriggersScreen
import com.github.mrbean355.admiralbulldog.sounds.sync.openSyncSoundBitesScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tornadofx.FX
import kotlin.concurrent.timer
import kotlin.system.exitProcess

private const val HEARTBEAT_FREQUENCY_MS = 30 * 1_000L
private const val ANALYTICS_FREQUENCY_MS = 5 * 60 * 1_000L

class MainViewModel : ComposeViewModel() {
    private val discordBotRepository = DiscordBotRepository()
    private val modRepository = DotaModRepository()
    private val updateViewModel = FX.find(UpdateViewModel::class.java)

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    val version: String = getString("lbl_app_version", APP_VERSION.value, getDistributionName())

    init {
        sendHeartbeats()
        //ensureValidDotaPath()
        OldModMigration.run()
        if (!ConfigPersistence.isModRiskAccepted()) {
            modRepository.uninstallAllMods()
        }
        ensureGsiInstalled()
        checkForNewSounds()
        checkForAppUpdate()

        if (FeedbackScreen.shouldPrompt()) {
            openFeedbackScreen()
        }

        monitorGameStateUpdates {
            viewModelScope.launch {
                _isConnected.value = true
            }
        }
    }

    private fun ensureValidDotaPath() {
        if (DotaPath.hasValidSavedPath()) {
            return
        }
        openInstallationWizard(onCancelled = {
            if (!DotaPath.hasValidSavedPath()) {
                showError(getString("header_install_gsi"), getString("content_installer_fail"))
                exitProcess(-1)
            }
        })
    }

    private fun ensureGsiInstalled() {
        val alreadyInstalled = GameStateIntegration.isInstalled()
        GameStateIntegration.install()
        // We don't show the information dialog here because it's handled by the screen/UI if needed, 
        // or we keep it as is for now.
    }

    fun onChangeSoundsClicked() {
        openViewSoundTriggersScreen()
    }

    fun onDiscordBotClicked() {
        openDiscordBotScreen()
    }

    fun onDotaModClicked() {
        openDotaModsScreen()
    }

    fun onRoshanTimerClicked() {
        openRoshanTimerScreen()
    }

    fun onSettingsClicked() {
        openSettingsScreen()
    }

    fun onVersionClicked() {
        // Handled by Screen using local browser launch
    }

    private fun checkForNewSounds() {
        if (updateViewModel.shouldCheckForNewSounds()) {
            openSyncSoundBitesScreen()
        } else {
            SoundBites.checkForInvalidSounds()
        }
    }

    private fun checkForAppUpdate() {
        if (!updateViewModel.shouldCheckForAppUpdate()) {
            checkForModUpdate()
            return
        }
        updateViewModel.checkForAppUpdate(
            onError = { checkForModUpdate() },
            onUpdateSkipped = { checkForModUpdate() },
            onNoUpdate = { checkForModUpdate() }
        )
    }

    private fun checkForModUpdate() {
        if (!updateViewModel.shouldCheckForModUpdate()) {
            return
        }
        updateViewModel.checkForModUpdates()
    }

    private fun sendHeartbeats() {
        timer(daemon = true, period = HEARTBEAT_FREQUENCY_MS) {
            viewModelScope.launch {
                discordBotRepository.sendHeartbeat()
            }
        }
        timer(daemon = true, period = ANALYTICS_FREQUENCY_MS) {
            viewModelScope.launch {
                logAnalyticsProperties()
            }
        }
    }
}