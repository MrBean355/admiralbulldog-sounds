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

package com.github.mrbean355.admiralbulldog.feedback

import com.github.mrbean355.admiralbulldog.AppStyles
import com.github.mrbean355.admiralbulldog.common.PADDING_MEDIUM
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.common.ratingSlider
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE
import javafx.scene.control.ButtonBar.ButtonData.FINISH
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
import tornadofx.textarea
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class FeedbackScreen : Fragment(getString("title_feedback")) {
    private val viewModel: FeedbackViewModel by inject(Scope())

    override val root = form {
        fieldset {
            label(getString("label_feedback_info")) {
                addClass(AppStyles.mediumFont)
                paddingBottom = PADDING_MEDIUM
            }
            field(getString("label_feedback_rating")) {
                ratingSlider(viewModel.rating)
            }
            field(getString("label_feedback_comments")) {
                textarea(viewModel.comments) {
                    promptText = getString("prompt_feedback_comments")
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

        fun shouldPrompt(): Boolean {
            val diff = System.currentTimeMillis() - ConfigPersistence.getFeedbackCompleted()
            val days = TimeUnit.MILLISECONDS.toDays(diff.coerceAtLeast(0))
            return days >= 90 && Random.nextDouble() <= 0.15
        }
    }
}