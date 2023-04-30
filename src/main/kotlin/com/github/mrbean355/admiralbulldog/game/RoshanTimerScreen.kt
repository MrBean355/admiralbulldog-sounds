/*
 * Copyright 2023 Michael Johnston
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

package com.github.mrbean355.admiralbulldog.game

import com.github.mrbean355.admiralbulldog.common.PADDING_MEDIUM
import com.github.mrbean355.admiralbulldog.common.PADDING_SMALL
import com.github.mrbean355.admiralbulldog.common.WINDOW_WIDTH
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.styles.AppStyles
import javafx.scene.control.ButtonBar.ButtonData.HELP
import tornadofx.Fragment
import tornadofx.Scope
import tornadofx.action
import tornadofx.addClass
import tornadofx.button
import tornadofx.buttonbar
import tornadofx.label
import tornadofx.paddingAll
import tornadofx.paddingTop
import tornadofx.vbox
import tornadofx.whenUndocked

class RoshanTimerScreen : Fragment(getString("header_roshan_timer")) {
    private val viewModel: RoshanTimerViewModel by inject(Scope())

    override val root = vbox(spacing = PADDING_SMALL) {
        prefWidth = WINDOW_WIDTH
        paddingAll = PADDING_MEDIUM

        label(viewModel.deathTime) {
            addClass(AppStyles.mediumFont)
        }
        label(viewModel.aegisExpiryTime) {
            addClass(AppStyles.mediumFont)
        }
        label(viewModel.respawnTime) {
            addClass(AppStyles.mediumFont)
        }

        label(getString("label_roshan_timer_turbo")) {
            paddingTop = PADDING_SMALL
            addClass(AppStyles.mediumFont)
            addClass(AppStyles.boldFont)
        }
        label(viewModel.aegisExpiryTimeTurbo) {
            addClass(AppStyles.mediumFont)
        }
        label(viewModel.respawnTimeTurbo) {
            addClass(AppStyles.mediumFont)
        }

        buttonbar {
            button(getString("btn_help"), type = HELP) {
                action {
                    viewModel.onHelpClicked()
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
