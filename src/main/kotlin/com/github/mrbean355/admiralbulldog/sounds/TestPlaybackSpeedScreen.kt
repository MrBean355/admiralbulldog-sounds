package com.github.mrbean355.admiralbulldog.sounds

import com.github.mrbean355.admiralbulldog.common.*
import javafx.geometry.Pos.CENTER_LEFT
import tornadofx.*

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
                    buttonImage = PlayIcon(),
                    buttonTooltip = getString("tooltip_play_locally"),
                    stringConverter = { it.name },
                    onButtonClicked = { item, _ -> item.play(rate = rate.get()) }
            )
        }
    }

    init {
        whenUndocked {
            viewModel.onUndock()
        }
    }
}