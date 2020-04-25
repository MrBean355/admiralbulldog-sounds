package com.github.mrbean355.admiralbulldog.ui2.settings

import com.github.mrbean355.admiralbulldog.ui.getString
import com.github.mrbean355.admiralbulldog.ui2.Spacing
import com.github.mrbean355.admiralbulldog.ui2.slider
import tornadofx.View
import tornadofx.field
import tornadofx.fieldset
import tornadofx.form
import tornadofx.paddingAll

class SettingsScreen : View(getString("tab_settings")) {
    private val viewModel = SettingsViewModel()

    override val root = form {
        paddingAll = Spacing.MEDIUM
        fieldset {
            field(getString("label_volume")) {
                slider(min = 0, max = 100, valueProperty = viewModel.volumeProperty)
            }
        }
    }
}