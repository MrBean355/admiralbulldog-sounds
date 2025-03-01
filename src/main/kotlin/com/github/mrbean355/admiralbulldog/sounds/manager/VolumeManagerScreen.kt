/*
 * Copyright 2024 Michael Johnston
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.mrbean355.admiralbulldog.sounds.manager

import com.github.mrbean355.admiralbulldog.common.AddIcon
import com.github.mrbean355.admiralbulldog.common.DeleteIcon
import com.github.mrbean355.admiralbulldog.common.HelpIcon
import com.github.mrbean355.admiralbulldog.common.PADDING_MEDIUM
import com.github.mrbean355.admiralbulldog.common.PADDING_SMALL
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.common.useVolumeCells
import javafx.scene.image.ImageView
import tornadofx.Fragment
import tornadofx.Scope
import tornadofx.action
import tornadofx.button
import tornadofx.enableWhen
import tornadofx.hbox
import tornadofx.listview
import tornadofx.onDoubleClick
import tornadofx.paddingAll
import tornadofx.spacer
import tornadofx.vbox

class VolumeManagerScreen : Fragment(getString("title_volume_manager")) {
    private val viewModel by inject<VolumeManagerViewModel>(Scope())

    override val root = vbox(spacing = PADDING_SMALL) {
        paddingAll = PADDING_MEDIUM
        listview(viewModel.items) {
            viewModel.selection.bind(selectionModel.selectedItemProperty())
            useVolumeCells()
            onDoubleClick {
                viewModel.onListDoubleClicked()
            }
        }
        hbox(spacing = PADDING_SMALL) {
            button(graphic = ImageView(HelpIcon())) {
                action { viewModel.onHelpClicked() }
            }
            spacer()
            button(graphic = ImageView(DeleteIcon())) {
                enableWhen(viewModel.hasSelection)
                action { viewModel.onRemoveVolumeClicked() }
            }
            button(graphic = ImageView(AddIcon())) {
                action { viewModel.onAddVolumeClicked() }
            }
        }
    }
}
