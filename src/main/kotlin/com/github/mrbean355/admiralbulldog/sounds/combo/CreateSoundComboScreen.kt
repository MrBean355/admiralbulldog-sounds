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