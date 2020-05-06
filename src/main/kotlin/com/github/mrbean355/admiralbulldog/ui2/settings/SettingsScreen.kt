package com.github.mrbean355.admiralbulldog.ui2.settings

import com.github.mrbean355.admiralbulldog.APP_VERSION
import com.github.mrbean355.admiralbulldog.ui.getString
import com.github.mrbean355.admiralbulldog.ui2.Spacing
import com.github.mrbean355.admiralbulldog.ui2.slider
import tornadofx.View
import tornadofx.action
import tornadofx.button
import tornadofx.field
import tornadofx.fieldset
import tornadofx.form
import tornadofx.label
import tornadofx.paddingAll

class SettingsScreen : View(getString("tab_settings")) {
    private val viewModel by inject<SettingsViewModel>()

    override val root = form {
        paddingAll = Spacing.MEDIUM
        fieldset {
            field(getString("label_volume")) {
                slider(min = 0, max = 100, valueProperty = viewModel.volumeProperty)
            }
        }
        fieldset(getString("section_about")) {
            field(getString("label_app_version")) {
                label(APP_VERSION.toString())
            }
            field {
                button(getString("action_check_for_update")) {
                    action { viewModel.onCheckForUpdateClicked() }
                }
            }
        }
    }
}