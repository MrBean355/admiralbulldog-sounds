package com.github.mrbean355.admiralbulldog.settings

import com.github.mrbean355.admiralbulldog.common.MAX_VOLUME
import com.github.mrbean355.admiralbulldog.common.MIN_VOLUME
import com.github.mrbean355.admiralbulldog.common.PADDING_MEDIUM
import com.github.mrbean355.admiralbulldog.common.PeriodStringConverter
import com.github.mrbean355.admiralbulldog.common.getString
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
import tornadofx.label
import tornadofx.managedWhen
import tornadofx.paddingAll
import tornadofx.spinner
import tornadofx.visibleWhen
import tornadofx.whenUndocked

class SettingsScreen : Fragment(getString("title_settings")) {
    private val viewModel by inject<SettingsViewModel>(Scope())

    override val root = form {
        paddingAll = PADDING_MEDIUM
        fieldset {
            field(getString("label_volume")) {
                spinner(min = MIN_VOLUME, max = MAX_VOLUME, amountToStepBy = 5, property = viewModel.appVolume, editable = true, enableScroll = true) {
                    valueFactory.converter = PeriodStringConverter()
                }
                label(getString("label_percentage"))
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
            field(getString("settings_field_mod_update")) {
                enableWhen(viewModel.modEnabled)
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