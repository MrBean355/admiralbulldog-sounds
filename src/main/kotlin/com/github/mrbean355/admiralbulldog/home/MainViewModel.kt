/*
 * Copyright 2024 Michael Johnston
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.mrbean355.admiralbulldog.home

import com.github.mrbean355.admiralbulldog.APP_VERSION
import com.github.mrbean355.admiralbulldog.arch.AppViewModel
import com.github.mrbean355.admiralbulldog.arch.logAnalyticsProperties
import com.github.mrbean355.admiralbulldog.arch.repo.DiscordBotRepository
import com.github.mrbean355.admiralbulldog.arch.repo.DotaModRepository
import com.github.mrbean355.admiralbulldog.assets.SoundBites
import com.github.mrbean355.admiralbulldog.common.PauseChampIcon
import com.github.mrbean355.admiralbulldog.common.PoggiesIcon
import com.github.mrbean355.admiralbulldog.common.URL_SPECIFIC_RELEASE
import com.github.mrbean355.admiralbulldog.common.getDistributionName
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.common.showError
import com.github.mrbean355.admiralbulldog.common.showInformation
import com.github.mrbean355.admiralbulldog.discord.DiscordBotScreen
import com.github.mrbean355.admiralbulldog.feedback.FeedbackScreen
import com.github.mrbean355.admiralbulldog.game.RoshanTimerScreen
import com.github.mrbean355.admiralbulldog.game.monitorGameStateUpdates
import com.github.mrbean355.admiralbulldog.installation.InstallationWizard
import com.github.mrbean355.admiralbulldog.mods.DotaModsScreen
import com.github.mrbean355.admiralbulldog.mods.OldModMigration
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import com.github.mrbean355.admiralbulldog.persistence.DotaPath
import com.github.mrbean355.admiralbulldog.persistence.GameStateIntegration
import com.github.mrbean355.admiralbulldog.settings.UpdateViewModel
import com.github.mrbean355.admiralbulldog.sounds.ViewSoundTriggersScreen
import com.github.mrbean355.admiralbulldog.sounds.sync.SyncSoundBitesScreen
import com.github.mrbean355.admiralbulldog.ui.openScreen
import javafx.beans.binding.Binding
import javafx.beans.binding.BooleanBinding
import javafx.beans.binding.StringBinding
import javafx.beans.property.StringProperty
import javafx.scene.control.ButtonType
import javafx.scene.image.Image
import kotlinx.coroutines.launch
import tornadofx.Scope
import tornadofx.booleanProperty
import tornadofx.find
import tornadofx.objectBinding
import tornadofx.runLater
import tornadofx.stringBinding
import tornadofx.stringProperty
import kotlin.concurrent.timer
import kotlin.system.exitProcess

private const val HEARTBEAT_FREQUENCY_MS = 30 * 1_000L
private const val ANALYTICS_FREQUENCY_MS = 5 * 60 * 1_000L

class MainViewModel : AppViewModel() {
    private val discordBotRepository = DiscordBotRepository()
    private val modRepository = DotaModRepository()
    private val updateViewModel by inject<UpdateViewModel>()
    private val hasHeardFromDota = booleanProperty(false)

    val image: Binding<Image?> = hasHeardFromDota.objectBinding {
        if (it == true) PoggiesIcon() else PauseChampIcon()
    }
    val heading: StringBinding = hasHeardFromDota.stringBinding {
        if (it == true) getString("msg_connected") else getString("msg_not_connected")
    }
    val progressBarVisible: BooleanBinding = hasHeardFromDota.not()
    val infoMessage: StringBinding = hasHeardFromDota.stringBinding {
        if (it == true) getString("dsc_connected") else getString("dsc_not_connected")
    }
    val version: StringProperty = stringProperty(getString("lbl_app_version", APP_VERSION.value, getDistributionName()))

    override fun onReady() {
        sendHeartbeats()

        ensureValidDotaPath()
        OldModMigration.run()
        if (!ConfigPersistence.isModRiskAccepted()) {
            modRepository.uninstallAllMods()
        }
        ensureGsiInstalled()

        checkForNewSounds()
        checkForAppUpdate()

        if (FeedbackScreen.shouldPrompt()) {
            openScreen<FeedbackScreen>()
        }

        monitorGameStateUpdates {
            runLater {
                hasHeardFromDota.set(true)
                primaryStage.sizeToScene()
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
            showError(getString("header_install_gsi"), getString("content_installer_fail"))
            exitProcess(-1)
        }
    }

    private fun ensureGsiInstalled() {
        val alreadyInstalled = GameStateIntegration.isInstalled()
        GameStateIntegration.install()
        if (!alreadyInstalled) {
            showInformation(getString("header_install_gsi"), getString("msg_installer_success"), ButtonType.FINISH)
        }
    }

    fun onChangeSoundsClicked() {
        openScreen<ViewSoundTriggersScreen>()
    }

    fun onDiscordBotClicked() {
        openScreen<DiscordBotScreen>()
    }

    fun onDotaModClicked() {
        openScreen<DotaModsScreen>()
    }

    fun onRoshanTimerClicked() {
        openScreen<RoshanTimerScreen>()
    }

    fun onVersionClicked() {
        hostServices.showDocument(URL_SPECIFIC_RELEASE.format(APP_VERSION.value))
    }

    private fun checkForNewSounds() {
        if (updateViewModel.shouldCheckForNewSounds()) {
            openScreen<SyncSoundBitesScreen>(escapeClosesWindow = false, block = true)
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