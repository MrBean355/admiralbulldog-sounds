package com.github.mrbean355.admiralbulldog.sounds

import com.github.mrbean355.admiralbulldog.common.PADDING_MEDIUM
import com.github.mrbean355.admiralbulldog.common.PADDING_SMALL
import com.github.mrbean355.admiralbulldog.common.SettingsIcon
import com.github.mrbean355.admiralbulldog.common.WINDOW_WIDTH
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.common.slider
import com.github.mrbean355.admiralbulldog.events.SOUND_EVENT_TYPES
import com.github.mrbean355.admiralbulldog.persistence.MAX_VOLUME
import com.github.mrbean355.admiralbulldog.persistence.MIN_VOLUME
import javafx.scene.control.Tooltip
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.Priority.ALWAYS
import tornadofx.Fragment
import tornadofx.Scope
import tornadofx.action
import tornadofx.button
import tornadofx.checkbox
import tornadofx.enableWhen
import tornadofx.gridpane
import tornadofx.gridpaneConstraints
import tornadofx.imageview
import tornadofx.label
import tornadofx.paddingAll
import tornadofx.row
import tornadofx.whenUndocked

class ToggleSoundEventsScreen : Fragment(getString("title_toggle_sound_triggers")) {
    private val viewModel by inject<ToggleSoundEventsViewModel>(Scope())

    override val root = gridpane {
        hgap = PADDING_SMALL
        vgap = PADDING_SMALL
        paddingAll = PADDING_MEDIUM
        prefWidth = WINDOW_WIDTH
        columnConstraints.addAll(ColumnConstraints().apply {
            hgrow = ALWAYS
        })
        row {
            label(getString("label_volume"))
        }
        row {
            slider(MIN_VOLUME, MAX_VOLUME, viewModel.volumeProperty) {
                gridpaneConstraints {
                    columnSpan = 3
                }
            }
        }
        SOUND_EVENT_TYPES.forEach { type ->
            row {
                val checkBox = checkbox(type.friendlyName, viewModel.enabledProperty(type)) {
                    tooltip = Tooltip(type.description)
                }
                button(graphic = imageview(SettingsIcon())) {
                    enableWhen(checkBox.selectedProperty())
                    action { viewModel.onConfigureClicked(type) }
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
