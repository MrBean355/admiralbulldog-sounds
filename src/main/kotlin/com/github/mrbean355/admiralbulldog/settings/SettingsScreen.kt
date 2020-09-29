package com.github.mrbean355.admiralbulldog.settings

import com.github.mrbean355.admiralbulldog.common.PADDING_MEDIUM
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.common.volumeSpinner
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
        }
        fieldset {
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
        hyperlink(getString("btn_project_website")) {
            action { viewModel.onProjectWebsiteClicked() }
        }
    }

    init {
        whenUndocked {
            viewModel.onUndock()
        }
    }
}