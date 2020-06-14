package com.github.mrbean355.admiralbulldog.sounds

import com.github.mrbean355.admiralbulldog.common.PADDING_MEDIUM
import com.github.mrbean355.admiralbulldog.common.PADDING_SMALL
import com.github.mrbean355.admiralbulldog.common.SettingsIcon
import com.github.mrbean355.admiralbulldog.common.WINDOW_WIDTH
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.sounds.manager.SoundManagerScreen
import com.github.mrbean355.admiralbulldog.triggers.SOUND_TRIGGER_TYPES
import javafx.scene.image.ImageView
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.Priority.ALWAYS
import tornadofx.Fragment
import tornadofx.Scope
import tornadofx.action
import tornadofx.button
import tornadofx.gridpane
import tornadofx.label
import tornadofx.paddingAll
import tornadofx.row
import tornadofx.whenUndocked

class ViewSoundTriggersScreen : Fragment(getString("title_toggle_sound_triggers")) {
    private val viewModel by inject<ViewSoundTriggersViewModel>(Scope())

    override val root = gridpane {
        hgap = PADDING_SMALL
        vgap = PADDING_SMALL
        paddingAll = PADDING_MEDIUM
        prefWidth = WINDOW_WIDTH
        columnConstraints.addAll(ColumnConstraints().apply {
            hgrow = ALWAYS
        })
        SOUND_TRIGGER_TYPES.forEach { type ->
            row {
                label(viewModel.textProperty(type)) {
                    textFillProperty().bind(viewModel.textColourProperty(type))
                }
                button(graphic = ImageView(SettingsIcon())) {
                    action { viewModel.onConfigureClicked(type) }
                }
            }
        }
        row {
            button(getString("btn_manage_sounds")) {
                action { find<SoundManagerScreen>().openModal(resizable = false) }
            }
        }
    }

    init {
        whenUndocked {
            viewModel.onUndock()
        }
    }
}
