/*
 * Copyright 2022 Michael Johnston
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

package com.github.mrbean355.admiralbulldog.sounds.manager

import com.github.mrbean355.admiralbulldog.common.MAX_INDIVIDUAL_VOLUME
import com.github.mrbean355.admiralbulldog.common.PADDING_MEDIUM
import com.github.mrbean355.admiralbulldog.common.PADDING_SMALL
import com.github.mrbean355.admiralbulldog.common.PlayIcon
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.common.volumeSpinner
import com.github.mrbean355.admiralbulldog.styles.AppStyles
import javafx.scene.control.ButtonBar
import javafx.scene.image.ImageView
import tornadofx.Fragment
import tornadofx.Scope
import tornadofx.action
import tornadofx.addClass
import tornadofx.button
import tornadofx.buttonbar
import tornadofx.enableWhen
import tornadofx.hbox
import tornadofx.label
import tornadofx.onChange
import tornadofx.paddingAll
import tornadofx.runLater
import tornadofx.textfield
import tornadofx.vbox

class ChooseVolumeScreen : Fragment(getString("title_choose_volume")) {
    private val viewModel by inject<ChooseVolumeViewModel>(Scope(), params)

    override val root = vbox(spacing = PADDING_SMALL) {
        paddingAll = PADDING_MEDIUM
        label(getString("label_search_sound_bite"))
        hbox(spacing = PADDING_SMALL) {
            textfield(viewModel.query) {
                textProperty().onChange {
                    // The caret moves to the start when auto-completing.
                    runLater(this::end)
                }
            }
            volumeSpinner(viewModel.volume, MAX_INDIVIDUAL_VOLUME)
            button(graphic = ImageView(PlayIcon())) {
                addClass(AppStyles.iconButton)
                enableWhen(viewModel.hasSoundBite)
                action {
                    viewModel.onPlayClicked()
                }
            }
        }
        buttonbar {
            button(getString("btn_done"), ButtonBar.ButtonData.OK_DONE) {
                enableWhen(viewModel.hasSoundBite)
                action {
                    viewModel.onDoneClicked()
                    close()
                }
            }
        }
    }

    companion object {
        fun params(name: String): Map<String, Any?> {
            return mapOf("name" to name)
        }
    }
}