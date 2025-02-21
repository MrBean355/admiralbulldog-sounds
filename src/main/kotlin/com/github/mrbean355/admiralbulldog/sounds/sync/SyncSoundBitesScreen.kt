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

package com.github.mrbean355.admiralbulldog.sounds.sync

import com.github.mrbean355.admiralbulldog.common.PADDING_MEDIUM
import com.github.mrbean355.admiralbulldog.common.PADDING_SMALL
import com.github.mrbean355.admiralbulldog.common.SoundBiteTreeModel
import com.github.mrbean355.admiralbulldog.common.WINDOW_WIDTH
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.common.showInformation
import com.github.mrbean355.admiralbulldog.common.useSoundBiteCells
import javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE
import javafx.scene.control.ButtonBar.ButtonData.OK_DONE
import javafx.scene.input.KeyCode.ESCAPE
import javafx.scene.input.KeyEvent
import tornadofx.Fragment
import tornadofx.Scope
import tornadofx.action
import tornadofx.button
import tornadofx.buttonbar
import tornadofx.fitToParentWidth
import tornadofx.managedWhen
import tornadofx.onChange
import tornadofx.paddingAll
import tornadofx.progressbar
import tornadofx.runLater
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
                visibleWhen(viewModel.finished.not())
                managedWhen(visibleProperty())
                action { close() }
            }
            button(getString("btn_done"), type = OK_DONE) {
                visibleWhen(viewModel.finished)
                managedWhen(visibleProperty())
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
        subscribe<SyncSoundBitesViewModel.NoUpdatesEvent> {
            showInformation(getString("header_latest_sound_bites"), getString("content_latest_sound_bites"))
            close()
        }
        subscribe<SyncSoundBitesViewModel.CloseEvent> {
            close()
        }
        runLater {
            currentStage?.addEventFilter(KeyEvent.KEY_PRESSED) {
                if (it.code == ESCAPE && viewModel.finished.get()) {
                    close()
                }
            }
        }
    }
}
