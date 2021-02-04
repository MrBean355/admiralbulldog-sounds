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

package com.github.mrbean355.admiralbulldog.settings

import com.github.mrbean355.admiralbulldog.arch.AppViewModel
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.common.showError
import com.github.mrbean355.admiralbulldog.common.showInformation
import com.github.mrbean355.admiralbulldog.common.showWarning
import com.github.mrbean355.admiralbulldog.feedback.FeedbackScreen
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import com.github.mrbean355.admiralbulldog.sounds.sync.SyncSoundBitesScreen
import com.github.mrbean355.admiralbulldog.ui.openScreen
import com.github.mrbean355.admiralbulldog.ui.refreshSystemTray
import javafx.beans.property.BooleanProperty
import javafx.beans.property.IntegerProperty
import javafx.beans.property.ObjectProperty
import tornadofx.booleanProperty
import tornadofx.intProperty
import tornadofx.objectProperty
import tornadofx.onChange
import java.awt.SystemTray

class SettingsViewModel : AppViewModel() {
    private val updateViewModel by inject<UpdateViewModel>()

    val appVolume: IntegerProperty = intProperty(ConfigPersistence.getVolume())
    val traySupported: BooleanProperty = booleanProperty(SystemTray.isSupported())
    val minimizeToTray: BooleanProperty = booleanProperty(ConfigPersistence.isMinimizeToTray())
    val alwaysShowTrayIcon: BooleanProperty = booleanProperty(ConfigPersistence.isAlwaysShowTrayIcon())

    val updateFrequencies: List<UpdateFrequency> = UpdateFrequency.values().toList()
    val appUpdateFrequency: ObjectProperty<UpdateFrequency> = objectProperty(ConfigPersistence.getAppUpdateFrequency())
    val soundsUpdateFrequency: ObjectProperty<UpdateFrequency> = objectProperty(ConfigPersistence.getSoundsUpdateFrequency())
    val modEnabled: BooleanProperty = booleanProperty(ConfigPersistence.hasEnabledMods())
    val modUpdateFrequency: ObjectProperty<UpdateFrequency> = objectProperty(ConfigPersistence.getModUpdateFrequency())

    init {
        appVolume.onChange { ConfigPersistence.setVolume(it) }
        minimizeToTray.onChange {
            ConfigPersistence.setMinimizeToTray(it)
            refreshSystemTray()
        }
        alwaysShowTrayIcon.onChange {
            ConfigPersistence.setAlwaysShowTrayIcon(it)
            refreshSystemTray()
        }
        appUpdateFrequency.onChange {
            it?.let { ConfigPersistence.setAppUpdateFrequency(it) }
        }
        soundsUpdateFrequency.onChange {
            it?.let { ConfigPersistence.setSoundsUpdateFrequency(it) }
        }
        modUpdateFrequency.onChange {
            it?.let {
                ConfigPersistence.setModUpdateFrequency(it)
                if (it > UpdateFrequency.DAILY) {
                    showWarning(getString("header_mod_update_frequency"), getString("content_mod_update_frequency"))
                }
            }
        }
    }

    override fun onUndock() {
        updateViewModel.onUndock()
        super.onUndock()
    }

    fun onCheckForAppUpdateClicked() {
        updateViewModel.checkForAppUpdate(
            onError = { showError(getString("header_update_check_failed"), getString("content_update_check_failed")) },
            onNoUpdate = { showInformation(getString("header_up_to_date"), getString("content_app_up_to_date")) }
        )
    }

    fun onUpdateSoundsClicked() {
        openScreen<SyncSoundBitesScreen>(escapeClosesWindow = false)
    }

    fun onCheckForModUpdateClicked() {
        updateViewModel.checkForModUpdates(
            onError = { showError(getString("header_update_check_failed"), getString("content_update_check_failed")) },
            onNoUpdate = { showInformation(getString("header_mods_up_to_date"), getString("content_mods_up_to_date")) })
    }

    fun onMoreInformationClicked() {
        openScreen<MoreInformationScreen>()
    }

    fun onSendFeedbackClicked() {
        openScreen<FeedbackScreen>()
    }
}