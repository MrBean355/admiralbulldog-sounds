package com.github.mrbean355.admiralbulldog.sounds.manager

import com.github.mrbean355.admiralbulldog.assets.SoundBite
import com.github.mrbean355.admiralbulldog.common.PADDING_MEDIUM
import com.github.mrbean355.admiralbulldog.common.PADDING_SMALL
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.common.useLabelWithPlayButton
import com.github.mrbean355.admiralbulldog.sounds.tableHeader
import com.github.mrbean355.admiralbulldog.triggers.SOUND_TRIGGER_TYPES
import javafx.scene.control.cell.CheckBoxTableCell
import tornadofx.Fragment
import tornadofx.Scope
import tornadofx.SmartResize
import tornadofx.action
import tornadofx.button
import tornadofx.column
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
        textfield(viewModel.searchQuery) {
            promptText = getString("prompt_search")
        }
        tableview(viewModel.items) {
            isEditable = true
            readonlyColumn(getString("column_sound_bite"), SoundBite::name) {
                weightedWidth(2)
                useLabelWithPlayButton { it.play() }
            }
            columnResizePolicy = SmartResize.POLICY
            SOUND_TRIGGER_TYPES.forEach { trigger ->
                column(trigger.tableHeader, CheckBoxTableCell::class) {
                    cellFactory = CheckBoxTableCell.forTableColumn {
                        viewModel.getCellProperty(trigger, it)
                    }
                    isReorderable = false
                    isSortable = false
                    isResizable = false
                    weightedWidth(1)
                }
            }
        }
        button(getString("btn_volume_manager")) {
            action {
                find<VolumeManagerScreen>().openModal(resizable = false)
            }
        }
    }
}
