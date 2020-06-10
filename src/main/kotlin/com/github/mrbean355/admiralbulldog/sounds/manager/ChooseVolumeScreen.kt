package com.github.mrbean355.admiralbulldog.sounds.manager

import com.github.mrbean355.admiralbulldog.common.MAX_INDIVIDUAL_VOLUME
import com.github.mrbean355.admiralbulldog.common.MIN_INDIVIDUAL_VOLUME
import com.github.mrbean355.admiralbulldog.common.PADDING_MEDIUM
import com.github.mrbean355.admiralbulldog.common.PADDING_SMALL
import com.github.mrbean355.admiralbulldog.common.PeriodStringConverter
import com.github.mrbean355.admiralbulldog.common.PlayIcon
import com.github.mrbean355.admiralbulldog.common.VOLUME_STEP
import com.github.mrbean355.admiralbulldog.common.getString
import javafx.scene.control.ButtonBar
import javafx.scene.image.ImageView
import tornadofx.Fragment
import tornadofx.Scope
import tornadofx.action
import tornadofx.button
import tornadofx.buttonbar
import tornadofx.enableWhen
import tornadofx.hbox
import tornadofx.label
import tornadofx.onChange
import tornadofx.paddingAll
import tornadofx.runLater
import tornadofx.spinner
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
            spinner(min = MIN_INDIVIDUAL_VOLUME, max = MAX_INDIVIDUAL_VOLUME, amountToStepBy = VOLUME_STEP, property = viewModel.volume, editable = true, enableScroll = true) {
                valueFactory.converter = PeriodStringConverter()
            }
            button(graphic = ImageView(PlayIcon())) {
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