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

package com.github.mrbean355.admiralbulldog.sounds.sync

import com.github.mrbean355.admiralbulldog.arch.AppViewModel
import com.github.mrbean355.admiralbulldog.assets.SoundBites
import com.github.mrbean355.admiralbulldog.common.RETRY_BUTTON
import com.github.mrbean355.admiralbulldog.common.SoundBiteTreeItem
import com.github.mrbean355.admiralbulldog.common.SoundBiteTreeModel
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.common.showError
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import javafx.beans.property.BooleanProperty
import javafx.beans.property.DoubleProperty
import javafx.beans.property.ObjectProperty
import javafx.scene.control.ButtonType
import javafx.scene.control.TreeItem
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import tornadofx.FXEvent
import tornadofx.booleanProperty
import tornadofx.doubleProperty
import tornadofx.objectProperty

class SyncSoundBitesViewModel : AppViewModel() {
    val finished: BooleanProperty = booleanProperty(false)
    val progress: DoubleProperty = doubleProperty()
    val tree: ObjectProperty<TreeItem<SoundBiteTreeModel>> = objectProperty()

    override fun onReady() {
        viewModelScope.launch {
            SoundBites.progress.collect(progress::set)
        }
        updateSounds()
    }

    private fun updateSounds() {
        viewModelScope.launch {
            val result = SoundBites.synchronise()
            if (result == null) {
                showErrorDialog()
                return@launch
            }
            val updateCount = with(result) { newSounds.size + changedSounds.size + deletedSounds.size + failedSounds.size }
            if (updateCount == 0) {
                ConfigPersistence.setSoundsLastUpdateToNow()
                fire(NoUpdatesEvent())
                return@launch
            }
            val root = TreeItem<SoundBiteTreeModel>().apply {
                children += SoundBiteTreeItem(getString("label_new_sounds"), result.newSounds)
                children += SoundBiteTreeItem(getString("label_changed_sounds"), result.changedSounds)
                children += SoundBiteTreeItem(getString("label_deleted_sounds"), result.deletedSounds)
                children += SoundBiteTreeItem(getString("label_failed_sounds"), result.failedSounds)
            }
            tree.set(root)
            finished.set(true)
            if (result.failedSounds.isEmpty()) {
                ConfigPersistence.setSoundsLastUpdateToNow()
            }
            SoundBites.checkForInvalidSounds()
        }
    }

    private fun showErrorDialog() {
        showError(getString("header_unknown_error"), getString("content_update_check_failed"), RETRY_BUTTON, ButtonType.CANCEL) {
            if (it === RETRY_BUTTON) {
                updateSounds()
            } else {
                fire(CloseEvent())
            }
        }
    }

    class NoUpdatesEvent : FXEvent()
    class CloseEvent : FXEvent()
}