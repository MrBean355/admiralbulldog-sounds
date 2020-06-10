package com.github.mrbean355.admiralbulldog.sounds.sync

import com.github.mrbean355.admiralbulldog.common.PADDING_MEDIUM
import com.github.mrbean355.admiralbulldog.common.PADDING_SMALL
import com.github.mrbean355.admiralbulldog.common.SoundBiteTreeModel
import com.github.mrbean355.admiralbulldog.common.WINDOW_WIDTH
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.common.useSoundBiteCells
import javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE
import javafx.scene.control.ButtonBar.ButtonData.OK_DONE
import tornadofx.Fragment
import tornadofx.Scope
import tornadofx.action
import tornadofx.button
import tornadofx.buttonbar
import tornadofx.enableWhen
import tornadofx.fitToParentWidth
import tornadofx.managedWhen
import tornadofx.onChange
import tornadofx.paddingAll
import tornadofx.progressbar
import tornadofx.treeview
import tornadofx.vbox
import tornadofx.visibleWhen
import tornadofx.whenUndocked

class SyncSoundBitesScreen : Fragment(getString("sync_sound_bites_title")) {
    private val viewModel by inject<SyncSoundBitesViewModel>(Scope())

    override val root = vbox(spacing = PADDING_SMALL) {
        paddingAll = PADDING_MEDIUM
        prefWidth = WINDOW_WIDTH
        progressbar(viewModel.progress) {
            fitToParentWidth()
            visibleWhen(viewModel.finished.not())
            managedWhen(visibleProperty())
        }
        treeview<SoundBiteTreeModel> {
            visibleWhen(viewModel.finished)
            managedWhen(visibleProperty())
            rootProperty().bind(viewModel.tree)
            isShowRoot = false
            useSoundBiteCells {
                it.play()
            }
        }
        buttonbar {
            button(getString("btn_cancel"), type = CANCEL_CLOSE) {
                action { close() }
            }
            button(getString("btn_done"), type = OK_DONE) {
                enableWhen(viewModel.finished)
                action { close() }
            }
        }
    }

    init {
        whenUndocked {
            viewModel.onUndock()
        }
        viewModel.finished.onChange {
            currentStage?.sizeToScene()
        }
        subscribe<SyncSoundBitesViewModel.CloseEvent> {
            close()
        }
    }
}
