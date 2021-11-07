/*
 * Copyright 2021 Michael Johnston
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.mrbean355.admiralbulldog.home

import com.github.mrbean355.admiralbulldog.APP_VERSION
import com.github.mrbean355.admiralbulldog.arch.logAnalyticsProperties
import com.github.mrbean355.admiralbulldog.arch.repo.DiscordBotRepository
import com.github.mrbean355.admiralbulldog.arch.repo.DotaModRepository
import com.github.mrbean355.admiralbulldog.common.URL_SPECIFIC_RELEASE
import com.github.mrbean355.admiralbulldog.common.getDistributionName
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.feedback.FeedbackScreen
import com.github.mrbean355.admiralbulldog.game.monitorGameStateUpdates
import com.github.mrbean355.admiralbulldog.persistence.GameStateIntegration
import com.github.mrbean355.admiralbulldog.tryBrowseUrl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.concurrent.timer

private const val HEARTBEAT_FREQUENCY_MS = 30 * 1_000L

class MainViewModel(
    private val viewModelScope: CoroutineScope
) {
    private val discordBotRepository = DiscordBotRepository()
    private val dotaModRepository = DotaModRepository()
    private val hasHeardFromDota = MutableStateFlow(false)

    val heading: Flow<String> = hasHeardFromDota.map {
        if (it) getString("msg_connected") else getString("msg_not_connected")
    }
    val progressBarVisible: Flow<Boolean> = hasHeardFromDota.map { !it }
    val infoMessage: Flow<String> = hasHeardFromDota.map {
        if (it) getString("dsc_connected") else getString("dsc_not_connected")
    }
    val version: String get() = getString("lbl_app_version", APP_VERSION.value, getDistributionName())

    init {
        sendHeartbeats()

        ensureValidDotaPath()
        ensureGsiInstalled()
        dotaModRepository.ensureModsAreInstalled()

        checkForNewSounds()
        checkForAppUpdate()

        if (FeedbackScreen.shouldPrompt()) {
            // TODO: openScreen<FeedbackScreen>()
        }

        monitorGameStateUpdates {
            hasHeardFromDota.value = true
        }
    }

    // TODO
    private fun ensureValidDotaPath() {
//        if (DotaPath.hasValidSavedPath()) {
//            return
//        }
//        find<InstallationWizard>(scope = Scope()).openModal(block = true, resizable = false)
//        if (!DotaPath.hasValidSavedPath()) {
//            showError(getString("header_install_gsi"), getString("content_installer_fail"))
//            exitProcess(-1)
//        }
    }

    private fun ensureGsiInstalled() {
        val alreadyInstalled = GameStateIntegration.isInstalled()
        GameStateIntegration.install()
        if (!alreadyInstalled) {
            // TODO
            // showInformation(getString("header_install_gsi"), getString("msg_installer_success"), ButtonType.FINISH)
        }
    }

    fun onChangeSoundsClicked() {
        // TODO: openScreen<ViewSoundTriggersScreen>()
    }

    fun onDiscordBotClicked() {
        // TODO: openScreen<DiscordBotScreen>()
    }

    fun onDotaModClicked() {
        // TODO: openScreen<DotaModsScreen>()
    }

    fun onVersionClicked() {
        tryBrowseUrl(URL_SPECIFIC_RELEASE.format(APP_VERSION))
    }

    fun onSettingsClicked() {
        // TODO: open settings
    }

    private fun checkForNewSounds() {
        // TODO
        /*if (updateViewModel.shouldCheckForNewSounds()) {
            openScreen<SyncSoundBitesScreen>(escapeClosesWindow = false, block = true)
        } else {
            SoundBites.checkForInvalidSounds()
        }*/
    }

    private fun checkForAppUpdate() {
        // TODO
        /*if (!updateViewModel.shouldCheckForAppUpdate()) {
                   checkForModUpdate()
                   return
               }
               updateViewModel.checkForAppUpdate(
                   onError = { checkForModUpdate() },
                   onUpdateSkipped = { checkForModUpdate() },
                   onNoUpdate = { checkForModUpdate() }
               )*/
    }

    private fun checkForModUpdate() {
        // TODO
        /*if (!updateViewModel.shouldCheckForModUpdate()) {
                  return
              }
              updateViewModel.checkForModUpdates()*/
    }

    private fun sendHeartbeats() {
        timer(daemon = true, period = HEARTBEAT_FREQUENCY_MS) {
            viewModelScope.launch {
                discordBotRepository.sendHeartbeat()
            }
        }
        viewModelScope.launch {
            logAnalyticsProperties()
        }
    }
}