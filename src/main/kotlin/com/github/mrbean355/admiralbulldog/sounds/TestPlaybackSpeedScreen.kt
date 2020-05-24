package com.github.mrbean355.admiralbulldog.sounds

import com.github.mrbean355.admiralbulldog.common.DEFAULT_RATE
import com.github.mrbean355.admiralbulldog.common.MAX_RATE
import com.github.mrbean355.admiralbulldog.common.MIN_RATE
import com.github.mrbean355.admiralbulldog.common.PADDING_MEDIUM
import com.github.mrbean355.admiralbulldog.common.PADDING_SMALL
import com.github.mrbean355.admiralbulldog.common.RateStringConverter
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.common.slider
import com.github.mrbean355.admiralbulldog.common.useLabelWithButton
import tornadofx.Fragment
import tornadofx.Scope
import tornadofx.doubleProperty
import tornadofx.listview
import tornadofx.paddingAll
import tornadofx.textfield
import tornadofx.vbox
import tornadofx.whenUndocked

class TestPlaybackSpeedScreen : Fragment(getString("title_test_playback")) {
    private val viewModel by inject<TestPlaybackSpeedViewModel>(Scope())
    private val rate = doubleProperty(DEFAULT_RATE)

    override val root = vbox(spacing = PADDING_SMALL) {
        paddingAll = PADDING_MEDIUM
        slider(min = MIN_RATE, max = MAX_RATE, valueProperty = rate) {
            labelFormatter = RateStringConverter()
        }
        textfield(viewModel.searchQuery) {
            promptText = getString("prompt_search")
        }
        listview(viewModel.displayItems) {
            useLabelWithButton(
                    stringConverter = { it.name },
                    onButtonClicked = { it.play(rate = rate.get()) }
            )
        }
    }

    init {
        whenUndocked {
            viewModel.onUndock()
        }
    }
}