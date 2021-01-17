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

package com.github.mrbean355.admiralbulldog.discord

import com.github.mrbean355.admiralbulldog.common.PADDING_MEDIUM
import com.github.mrbean355.admiralbulldog.common.PADDING_SMALL
import com.github.mrbean355.admiralbulldog.common.WINDOW_WIDTH
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import com.github.mrbean355.admiralbulldog.sounds.SoundBiteTracker
import javafx.scene.control.ButtonBar.ButtonData.OK_DONE
import tornadofx.Fragment
import tornadofx.action
import tornadofx.button
import tornadofx.buttonbar
import tornadofx.paddingAll
import tornadofx.vbox

class ConfigureSoundBoardScreen : Fragment(getString("title_customise_sound_board")) {
    private val tracker = SoundBiteTracker(ConfigPersistence.getSoundBoard())

    override val root = vbox(spacing = PADDING_SMALL) {
        paddingAll = PADDING_MEDIUM
        prefWidth = WINDOW_WIDTH
        children += tracker.createSearchField()
        children += tracker.createListView()
        buttonbar {
            button(getString("btn_save"), OK_DONE) {
                action { onSaveClicked() }
            }
        }
    }

    private fun onSaveClicked() {
        ConfigPersistence.setSoundBoard(tracker.getSelection())
        close()
    }
}