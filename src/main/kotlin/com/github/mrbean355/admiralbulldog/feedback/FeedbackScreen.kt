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