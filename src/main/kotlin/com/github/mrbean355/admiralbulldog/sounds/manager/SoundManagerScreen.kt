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

package com.github.mrbean355.admiralbulldog.sounds.manager

import com.github.mrbean355.admiralbulldog.assets.SoundBite
import com.github.mrbean355.admiralbulldog.common.PADDING_MEDIUM
import com.github.mrbean355.admiralbulldog.common.PADDING_SMALL
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.common.useLabelWithPlayButton
import com.github.mrbean355.admiralbulldog.sounds.tableHeader
import com.github.mrbean355.admiralbulldog.triggers.SOUND_TRIGGER_TYPES
import javafx.geometry.Pos.CENTER_LEFT
import javafx.scene.control.cell.CheckBoxTableCell
import tornadofx.Fragment
import tornadofx.Scope
import tornadofx.SmartResize
import tornadofx.action
import tornadofx.button
import tornadofx.checkbox
import tornadofx.column
import tornadofx.hbox
import tornadofx.label
import tornadofx.paddingAll
import tornadofx.readonlyColumn
import tornadofx.tableview
import tornadofx.textfield
import tornadofx.vbox
import tornadofx.weightedWidth

class SoundManagerScreen : Fragment(getString("title_sound_bite_manager")) {
    private val viewModel by inject<SoundManagerViewModel>(Scope())

    override val root = vbox(spacing = PADDING_SMALL) {
        paddingAll = PADDING_MEDIUM
        hbox(spacing = PADDING_SMALL, alignment = CENTER_LEFT) {
            textfield(viewModel.searchQuery) {
                promptText = getString("prompt_search")
            }
            label(getString("label_filter_sound_bites"))
            checkbox(getString("label_filter_play_sounds"), viewModel.showPlaySounds)
            checkbox(getString("label_filter_sound_combos"), viewModel.showCombos)
            checkbox(getString("label_filter_used"), viewModel.showUsed)
            checkbox(getString("label_filter_unused"), viewModel.showUnused)
        }
        tableview(viewModel.items) {
            isEditable = true
            readonlyColumn(getString("column_sound_bite"), SoundBite::name) {
                weightedWidth(2)
                useLabelWithPlayButton { it.play() }
                isResizable = false
            }
            columnResizePolicy = SmartResize.POLICY
            SOUND_TRIGGER_TYPES.forEach { trigger ->
                column(trigger.tableHeader, CheckBoxTableCell::class) {
                    cellFactory = CheckBoxTableCell.forTableColumn {
                        viewModel.getCellProperty(trigger, it)
                    }
                    isSortable = false
                    isResizable = false
                    weightedWidth(1)
                }
            }
        }
        hbox(spacing = PADDING_SMALL) {
            button(getString("btn_volume_manager")) {
                action { viewModel.onManageVolumesClicked() }
            }
            button(getString("btn_sound_combos")) {
                action { viewModel.onSoundCombosClicked() }
            }
        }
    }
}
