package com.github.mrbean355.admiralbulldog.sounds.combo

import com.github.mrbean355.admiralbulldog.assets.ComboSoundBite
import com.github.mrbean355.admiralbulldog.assets.SoundBite
import com.github.mrbean355.admiralbulldog.common.AddIcon
import com.github.mrbean355.admiralbulldog.common.DeleteIcon
import com.github.mrbean355.admiralbulldog.common.PADDING_MEDIUM
import com.github.mrbean355.admiralbulldog.common.PADDING_SMALL
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.common.useLabelWithButton
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCode.ENTER
import javafx.scene.input.KeyEvent.KEY_PRESSED
import tornadofx.Fragment
import tornadofx.Scope
import tornadofx.action
import tornadofx.button
import tornadofx.enableWhen
import tornadofx.hbox
import tornadofx.label
import tornadofx.listview
import tornadofx.onChange
import tornadofx.paddingAll
import tornadofx.runLater
import tornadofx.textfield
import tornadofx.vbox

class CreateSoundComboScreen : Fragment(getString("title_create_sound_combo")) {
    private val viewModel by inject<CreateSoundComboViewModel>(Scope(), params)

    override val root = vbox(spacing = PADDING_SMALL) {
        paddingAll = PADDING_MEDIUM
        label(getString("label_sound_combo_name"))
        textfield(viewModel.name)
        label(getString("label_sound_combo_search"))
        hbox {
            textfield(viewModel.query) {
                promptText = getString("prompt_search")
                textProperty().onChange {
                    // The caret moves to the start when auto-completing.
                    runLater(this::end)
                }
                addEventFilter(KEY_PRESSED) {
                    if (it.code == ENTER && viewModel.hasSoundBite.value) {
                        viewModel.onAddClicked()
                    }
                }
            }
            button(graphic = ImageView(AddIcon())) {
                enableWhen(viewModel.hasSoundBite)
                action { viewModel.onAddClicked() }
            }
        }
        listview(viewModel.items) {
            useLabelWithButton(DeleteIcon(), getString("tooltip_remove"), SoundBite::name) { _, index ->
                viewModel.onRemoveClicked(index)
            }
        }
        hbox(spacing = PADDING_SMALL) {
            button(getString("btn_save")) {
                enableWhen(viewModel.canSave)
                action {
                    if (viewModel.onSaveClicked()) {
                        close()
                    }
                }
            }
            button(getString("btn_test_sound_combo")) {
                action { viewModel.onPlayClicked() }
            }
        }
    }

    companion object {
        fun params(soundBite: ComboSoundBite): Map<String, Any?> {
            return mapOf("soundBite" to soundBite)
        }
    }
}