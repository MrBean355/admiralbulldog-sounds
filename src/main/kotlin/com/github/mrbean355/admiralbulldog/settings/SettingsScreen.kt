package com.github.mrbean355.admiralbulldog.settings

import com.github.mrbean355.admiralbulldog.common.WINDOW_WIDTH
import com.github.mrbean355.admiralbulldog.common.getString
import tornadofx.Fragment
import tornadofx.Scope
import tornadofx.action
import tornadofx.button
import tornadofx.choicebox
import tornadofx.field
import tornadofx.fieldset
import tornadofx.form
import tornadofx.whenUndocked

class SettingsScreen : Fragment(getString("title_settings")) {
    private val viewModel by inject<SettingsViewModel>(Scope())

    override val root = form {
        prefWidth = WINDOW_WIDTH
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
            field(getString("settings_field_mod_update")) {
                choicebox(viewModel.modUpdateFrequency, viewModel.updateFrequencies) {
                    converter = UpdateFrequencyStringConverter()
                }
                button(getString("btn_check_now")) {
                    action { viewModel.onCheckForModUpdateClicked() }
                }
            }
        }
    }

    init {
        whenUndocked {
            viewModel.onUndock()
        }
    }
}