package com.github.mrbean355.admiralbulldog.sounds

import com.github.mrbean355.admiralbulldog.common.DEFAULT_RATE
import com.github.mrbean355.admiralbulldog.common.PADDING_MEDIUM
import com.github.mrbean355.admiralbulldog.common.PADDING_SMALL
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.common.rateSpinner
import com.github.mrbean355.admiralbulldog.common.useLabelWithButton
import javafx.geometry.Pos.CENTER_LEFT
import tornadofx.Fragment
import tornadofx.Scope
import tornadofx.hbox
import tornadofx.intProperty
import tornadofx.label
import tornadofx.listview
import tornadofx.paddingAll
import tornadofx.textfield
import tornadofx.vbox
import tornadofx.whenUndocked

class TestPlaybackSpeedScreen : Fragment(getString("title_test_playback")) {
    private val viewModel by inject<TestPlaybackSpeedViewModel>(Scope())
    private val rate = intProperty(DEFAULT_RATE)

    override val root = vbox(spacing = PADDING_SMALL) {
        paddingAll = PADDING_MEDIUM
        hbox(spacing = PADDING_MEDIUM, alignment = CENTER_LEFT) {
            label(getString("label_playback_speed"))
            rateSpinner(rate)
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