package com.github.mrbean355.admiralbulldog.ui2.sounds

import com.github.mrbean355.admiralbulldog.ui.getString
import com.github.mrbean355.admiralbulldog.ui2.Spacing
import com.github.mrbean355.admiralbulldog.ui2.Text
import com.github.mrbean355.admiralbulldog.ui2.Window
import com.github.mrbean355.admiralbulldog.ui2.restrictMax
import com.github.mrbean355.admiralbulldog.ui2.restrictMin
import com.github.mrbean355.admiralbulldog.ui2.slider
import com.github.mrbean355.admiralbulldog.ui2.triggers.SoundTrigger
import javafx.scene.control.Slider
import javafx.scene.text.Font
import tornadofx.Fragment
import tornadofx.Scope
import tornadofx.action
import tornadofx.button
import tornadofx.checkbox
import tornadofx.field
import tornadofx.fieldset
import tornadofx.form
import tornadofx.label
import tornadofx.paddingAll
import tornadofx.paddingBottom

class ConfigureSoundTriggerScreen : Fragment() {
    private val soundTrigger by param<SoundTrigger>()
    private val viewModel by inject<ConfigureSoundTriggerViewModel>(Scope(), params)

    override val root = form {
        paddingAll = Spacing.MEDIUM
        label(viewModel.description) {
            isWrapText = true
            paddingBottom = Spacing.MEDIUM
            font = Font(Text.MEDIUM)
        }
        fieldset {
            field(getString("label_enabled")) {
                checkbox(property = viewModel.enabled)
            }
            field(getString("label_trigger_chance")) {
                slider(0, 100, viewModel.chance)
            }
        }
        fieldset(getString("label_playback_rate")) {
            var min: Slider? = null
            var max: Slider? = null
            field(getString("label_min_rate")) {
                min = slider(50, 200, viewModel.minRate)
            }
            field(getString("label_max_rate")) {
                max = slider(50, 200, viewModel.maxRate)
            }
            min?.let { min ->
                max?.let { max ->
                    min.restrictMax(max)
                    max.restrictMin(min)
                }
            }
        }
        fieldset {
            button(getString("button_choose_sounds")) {
                action {
                    find<ChooseSoundBitesScreen>(params = ChooseSoundBitesScreen.params(soundTrigger))
                            .openModal(resizable = false)
                }
            }
        }
        titleProperty.bind(viewModel.title)
        prefWidth = Window.WIDTH
    }

    companion object {
        fun params(soundTrigger: SoundTrigger): Map<*, Any?> {
            return mapOf("soundTrigger" to soundTrigger)
        }
    }
}