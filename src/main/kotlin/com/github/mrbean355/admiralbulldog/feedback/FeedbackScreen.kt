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

package com.github.mrbean355.admiralbulldog.feedback

import com.github.mrbean355.admiralbulldog.common.PADDING_MEDIUM
import com.github.mrbean355.admiralbulldog.common.PADDING_SMALL
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.styles.AppStyles
import javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE
import javafx.scene.control.ButtonBar.ButtonData.FINISH
import javafx.scene.control.TextFormatter
import tornadofx.Fragment
import tornadofx.Scope
import tornadofx.action
import tornadofx.addClass
import tornadofx.button
import tornadofx.buttonbar
import tornadofx.field
import tornadofx.fieldset
import tornadofx.form
import tornadofx.label
import tornadofx.paddingBottom
import tornadofx.paddingLeft
import tornadofx.radiobutton
import tornadofx.textarea
import tornadofx.togglegroup

class FeedbackScreen : Fragment(getString("title_feedback")) {
    private val viewModel: FeedbackViewModel by inject(Scope())

    override val root = form {
        fieldset {
            label(getString("label_feedback_info")) {
                addClass(AppStyles.mediumFont)
                paddingBottom = PADDING_MEDIUM
            }
            field(getString("label_feedback_rating")) {
                togglegroup(viewModel.rating) {
                    radiobutton("1")
                    radiobutton("2") {
                        paddingLeft = PADDING_SMALL
                    }
                    radiobutton("3") {
                        paddingLeft = PADDING_SMALL
                    }
                    radiobutton("4") {
                        paddingLeft = PADDING_SMALL
                    }
                    radiobutton("5") {
                        paddingLeft = PADDING_SMALL
                    }
                }
            }
            field(getString("label_feedback_comments")) {
                textarea(viewModel.comments) {
                    promptText = getString("prompt_feedback_comments")
                    textFormatter = TextFormatter<String> {
                        if (it.controlNewText.length <= 1_000) it else null
                    }
                }
            }
            buttonbar {
                button(getString("btn_no_thanks"), type = CANCEL_CLOSE) {
                    action { close() }
                }
                button(getString("btn_submit"), type = FINISH) {
                    action { viewModel.onSubmitClicked() }
                }
            }
        }
    }

    init {
        subscribe<FeedbackViewModel.CloseEvent> {
            close()
        }
    }

    companion object {

        fun shouldPrompt(): Boolean = FeedbackViewModel.shouldPrompt()

    }
}