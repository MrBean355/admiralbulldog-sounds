/*
 * Copyright 2022 Michael Johnston
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

package com.github.mrbean355.admiralbulldog.settings

import com.github.mrbean355.admiralbulldog.common.PADDING_MEDIUM
import com.github.mrbean355.admiralbulldog.common.PADDING_SMALL
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.common.volumeSpinner
import javafx.geometry.Pos.CENTER
import tornadofx.Fragment
import tornadofx.Scope
import tornadofx.action
import tornadofx.button
import tornadofx.checkbox
import tornadofx.choicebox
import tornadofx.enableWhen
import tornadofx.field
import tornadofx.fieldset
import tornadofx.form
import tornadofx.hbox
import tornadofx.hyperlink
import tornadofx.managedWhen
import tornadofx.paddingAll
import tornadofx.visibleWhen
import tornadofx.whenUndocked

class SettingsScreen : Fragment(getString("title_settings")) {
    private val viewModel by inject<SettingsViewModel>(Scope())

    override val root = form {
        paddingAll = PADDING_MEDIUM
        fieldset {
            field(getString("label_volume")) {
                volumeSpinner(viewModel.appVolume)
            }
            field(getString("label_dark_mode")) {
                checkbox(property = viewModel.darkMode)
            }
        }
        fieldset(getString("settings_header_system_tray")) {
            visibleWhen(viewModel.traySupported)
            managedWhen(visibleProperty())
            field(getString("label_minimise_to_tray")) {
                checkbox(property = viewModel.minimizeToTray)
            }
            field(getString("label_always_show_tray_icon")) {
                checkbox(property = viewModel.alwaysShowTrayIcon)
            }
        }
        fieldset(getString("settings_header_updates")) {
            field(getString("settings_field_app_update")) {
                choicebox(viewModel.appUpdateFrequency, viewModel.updateFrequencies) {
                    converter = UpdateFrequencyStringConverter()
                }
                button(getString("btn_check_now")) {
                    action { viewModel.onCheckForAppUpdateClicked() }
                }
            }
            field(getString("settings_field_sounds_update")) {
                choicebox(viewModel.soundsUpdateFrequency, viewModel.updateFrequencies) {
                    converter = UpdateFrequencyStringConverter()
                }
                button(getString("btn_check_now")) {
                    action { viewModel.onUpdateSoundsClicked() }
                }
            }
            field(getString("settings_field_mod_updates")) {
                enableWhen(viewModel.modEnabled)
                choicebox(viewModel.modUpdateFrequency, viewModel.updateFrequencies) {
                    converter = UpdateFrequencyStringConverter()
                }
                button(getString("btn_check_now")) {
                    action { viewModel.onCheckForModUpdateClicked() }
                }
            }
        }
        hbox(spacing = PADDING_SMALL, alignment = CENTER) {
            hyperlink(getString("btn_more_information")) {
                action { viewModel.onMoreInformationClicked() }
            }
            hyperlink(getString("btn_send_feedback")) {
                action { viewModel.onSendFeedbackClicked() }
            }
        }
    }

    init {
        whenUndocked {
            viewModel.onUndock()
        }
    }
}