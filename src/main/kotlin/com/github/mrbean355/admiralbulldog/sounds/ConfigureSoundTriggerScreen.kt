package com.github.mrbean355.admiralbulldog.sounds

import com.github.mrbean355.admiralbulldog.common.HelpIcon
import com.github.mrbean355.admiralbulldog.common.MAX_CHANCE
import com.github.mrbean355.admiralbulldog.common.MAX_PERIOD
import com.github.mrbean355.admiralbulldog.common.MAX_RATE
import com.github.mrbean355.admiralbulldog.common.MIN_CHANCE
import com.github.mrbean355.admiralbulldog.common.MIN_PERIOD
import com.github.mrbean355.admiralbulldog.common.MIN_RATE
import com.github.mrbean355.admiralbulldog.common.PADDING_MEDIUM
import com.github.mrbean355.admiralbulldog.common.PADDING_SMALL
import com.github.mrbean355.admiralbulldog.common.PeriodStringConverter
import com.github.mrbean355.admiralbulldog.common.RateStringConverter
import com.github.mrbean355.admiralbulldog.common.WINDOW_WIDTH_LARGE
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.common.information
import com.github.mrbean355.admiralbulldog.common.slider
import com.github.mrbean355.admiralbulldog.common.useHeaderFont
import com.github.mrbean355.admiralbulldog.events.SoundTrigger
import javafx.scene.image.ImageView
import tornadofx.Fragment
import tornadofx.Scope
import tornadofx.action
import tornadofx.button
import tornadofx.checkbox
import tornadofx.enableWhen
import tornadofx.field
import tornadofx.fieldset
import tornadofx.form
import tornadofx.hbox
import tornadofx.label
import tornadofx.managedWhen
import tornadofx.paddingAll
import tornadofx.paddingBottom
import tornadofx.spinner
import tornadofx.visibleWhen
import tornadofx.whenUndocked
import kotlin.reflect.KClass

class ConfigureSoundTriggerScreen : Fragment() {
    private val viewModel by inject<ConfigureSoundTriggerViewModel>(Scope(), params)

    override val root = form {
        prefWidth = WINDOW_WIDTH_LARGE
        paddingAll = PADDING_MEDIUM
        fieldset {
            label(viewModel.description) {
                paddingBottom = PADDING_SMALL
                useHeaderFont()
                isWrapText = true
            }
            field(getString("label_enable_sound_trigger")) {
                checkbox(property = viewModel.enabled)
            }
        }
        fieldset(getString("header_chance_to_play")) {
            visibleWhen(viewModel.showChance)
            managedWhen(visibleProperty())
            field(getString("label_use_smart_chance")) {
                visibleWhen(viewModel.showSmartChance)
                managedWhen(visibleProperty())
                checkbox(property = viewModel.useSmartChance)
                button(graphic = ImageView(HelpIcon())) {
                    action { information(getString("header_about_smart_chance"), getString("content_about_smart_chance")) }
                }
            }
            field(getString("label_chance_to_play")) {
                slider(min = MIN_CHANCE, max = MAX_CHANCE, valueProperty = viewModel.chance) {
                    enableWhen(viewModel.enableChanceSlider)
                }
            }
        }
        fieldset(getString("header_periodic_trigger")) {
            visibleWhen(viewModel.showPeriod)
            managedWhen(visibleProperty())
            field(getString("label_periodic_trigger_min")) {
                spinner(min = MIN_PERIOD, max = MAX_PERIOD, property = viewModel.minPeriod, editable = true, enableScroll = true) {
                    valueFactory.converter = PeriodStringConverter()
                }
                label(getString("label_periodic_trigger_minutes"))
            }
            field(getString("label_periodic_trigger_max")) {
                spinner(min = MIN_PERIOD, max = MAX_PERIOD, property = viewModel.maxPeriod, editable = true, enableScroll = true) {
                    valueFactory.converter = PeriodStringConverter()
                }
                label(getString("label_periodic_trigger_minutes"))
                button(graphic = ImageView(HelpIcon())) {
                    action { information(getString("header_about_periodic_trigger"), getString("content_about_periodic_trigger")) }
                }
            }
        }
        fieldset(getString("header_playback_speed")) {
            field(getString("label_min_playback_speed")) {
                slider(min = MIN_RATE, max = MAX_RATE, valueProperty = viewModel.minRate) {
                    labelFormatter = RateStringConverter()
                }
            }
            field(getString("label_max_playback_speed")) {
                slider(min = MIN_RATE, max = MAX_RATE, valueProperty = viewModel.maxRate) {
                    labelFormatter = RateStringConverter()
                }
            }
            hbox(spacing = PADDING_SMALL) {
                button(graphic = ImageView(HelpIcon())) {
                    action { information(getString("header_about_playback_speed"), getString("content_about_playback_speed")) }
                }
                button(getString("btn_test_playback_speed")) {
                    action { find<TestPlaybackSpeedScreen>().openModal(resizable = false) }
                }
            }
        }
        fieldset(getString("header_sound_bite_selection")) {
            field(getString("label_sound_bite_count")) {
                label(viewModel.soundBiteCount)
            }
            button(getString("btn_choose_sounds")) {
                action { viewModel.onChooseSoundsClicked() }
            }
        }
    }

    init {
        titleProperty.bind(viewModel.title)
        whenUndocked {
            viewModel.onUndock()
        }
    }

    companion object {

        fun params(type: KClass<out SoundTrigger>): Map<String, Any?> {
            return mapOf("type" to type)
        }
    }
}