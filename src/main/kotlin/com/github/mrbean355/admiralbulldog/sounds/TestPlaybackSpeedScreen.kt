/*
 * Copyright 2022 Michael Johnston
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

package com.github.mrbean355.admiralbulldog.sounds

import com.github.mrbean355.admiralbulldog.common.DEFAULT_RATE
import com.github.mrbean355.admiralbulldog.common.PADDING_MEDIUM
import com.github.mrbean355.admiralbulldog.common.PADDING_SMALL
import com.github.mrbean355.admiralbulldog.common.PlayIcon
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