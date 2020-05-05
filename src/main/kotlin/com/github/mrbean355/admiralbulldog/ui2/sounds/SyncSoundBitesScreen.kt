package com.github.mrbean355.admiralbulldog.ui2.sounds

import com.github.mrbean355.admiralbulldog.ui.getString
import com.github.mrbean355.admiralbulldog.ui.toNullable
import com.github.mrbean355.admiralbulldog.ui2.Spacing
import com.github.mrbean355.admiralbulldog.ui2.Text
import com.github.mrbean355.admiralbulldog.ui2.alert
import javafx.scene.control.Alert
import javafx.scene.control.ButtonBar
import javafx.scene.control.ButtonType
import javafx.scene.text.Font
import tornadofx.Fragment
import tornadofx.fitToParentWidth
import tornadofx.label
import tornadofx.managedWhen
import tornadofx.onChange
import tornadofx.paddingAll
import tornadofx.progressbar
import tornadofx.treeview
import tornadofx.vbox
import tornadofx.visibleWhen

class SyncSoundBitesScreen : Fragment(getString("title_update_sound_bites")) {
    private val viewModel = SyncSoundBitesViewModel()

    override val root = vbox(spacing = Spacing.SMALL) {
        paddingAll = Spacing.MEDIUM
        label(viewModel.header) {
            font = Font(Text.MEDIUM)
        }
        progressbar(viewModel.progress) {
            fitToParentWidth()
            visibleWhen(viewModel.complete.not())
            managedWhen(visibleProperty())
        }
        treeview<SoundBiteTreeCell.Model> {
            rootProperty().bind(viewModel.tree)
            isShowRoot = false
            setCellFactory {
                SoundBiteTreeCell()
            }
        }
        viewModel.complete.onChange {
            currentStage?.sizeToScene()
        }
        viewModel.error.onChange {
            val retry = ButtonType(getString("action_retry"), ButtonBar.ButtonData.NEXT_FORWARD)
            val action = alert(
                    type = Alert.AlertType.ERROR,
                    header = getString("header_update_sound_bites_failed"),
                    content = getString("content_update_sound_bites_failed"),
                    buttons = arrayOf(ButtonType.CANCEL, retry)
            ).showAndWait().toNullable()

            if (action === retry) {
                viewModel.onRetryClicked()
            } else {
                close()
            }
        }
    }
}
