/*
 * Copyright 2024 Michael Johnston
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.mrbean355.admiralbulldog.sounds

import com.github.mrbean355.admiralbulldog.common.HelpIcon
import com.github.mrbean355.admiralbulldog.common.PADDING_MEDIUM
import com.github.mrbean355.admiralbulldog.common.PADDING_SMALL
import com.github.mrbean355.admiralbulldog.common.WINDOW_WIDTH
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.triggers.SOUND_TRIGGER_TYPES
import javafx.geometry.Pos.CENTER_LEFT
import javafx.scene.image.ImageView
import tornadofx.Fragment
import tornadofx.Scope
import tornadofx.action
import tornadofx.button
import tornadofx.fitToParentWidth
import tornadofx.hbox
import tornadofx.label
import tornadofx.paddingAll
import tornadofx.paddingHorizontal
import tornadofx.paddingTop
import tornadofx.paddingVertical
import tornadofx.spacer
import tornadofx.vbox
import tornadofx.whenUndocked

class ViewSoundTriggersScreen : Fragment(getString("title_toggle_sound_triggers")) {
    private val viewModel by inject<ViewSoundTriggersViewModel>(Scope())

    override val root = vbox {
        paddingTop = PADDING_SMALL
        prefWidth = WINDOW_WIDTH
        SOUND_TRIGGER_TYPES.forEach { type ->
            label(viewModel.textProperty(type)) {
                paddingVertical = PADDING_SMALL * 2
                paddingHorizontal = PADDING_MEDIUM * 2
                fitToParentWidth()
                textFillProperty().bind(viewModel.textColourProperty(type))
                setOnMouseEntered {
                    background = viewModel.highlightedBackground
                }
                setOnMouseExited {
                    background = viewModel.normalBackground
                }
                setOnMouseClicked {
                    viewModel.onConfigureClicked(type)
                }
            }
        }
        hbox(alignment = CENTER_LEFT) {
            paddingAll = PADDING_MEDIUM
            button(getString("btn_manage_sounds")) {
                action { viewModel.onManageSoundsClicked() }
            }
            spacer()
            button(graphic = ImageView(HelpIcon())) {
                action { viewModel.onHelpClicked() }
            }
        }
    }

    init {
        whenUndocked {
            viewModel.onUndock()
        }
    }
}
