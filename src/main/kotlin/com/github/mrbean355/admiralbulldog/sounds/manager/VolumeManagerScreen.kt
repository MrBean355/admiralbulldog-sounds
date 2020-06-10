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
