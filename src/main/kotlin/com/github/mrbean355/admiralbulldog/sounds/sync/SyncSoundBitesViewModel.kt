package com.github.mrbean355.admiralbulldog.sounds.sync

import com.github.mrbean355.admiralbulldog.arch.AppViewModel
import com.github.mrbean355.admiralbulldog.assets.SoundBites
import com.github.mrbean355.admiralbulldog.common.RETRY_BUTTON
import com.github.mrbean355.admiralbulldog.common.SoundBiteTreeItem
import com.github.mrbean355.admiralbulldog.common.SoundBiteTreeModel
import com.github.mrbean355.admiralbulldog.common.error
import com.github.mrbean355.admiralbulldog.common.getString
import javafx.beans.property.BooleanProperty
import javafx.beans.property.DoubleProperty
import javafx.beans.property.ObjectProperty
import javafx.scene.control.ButtonType
import javafx.scene.control.ProgressIndicator.INDETERMINATE_PROGRESS
import javafx.scene.control.TreeItem
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tornadofx.FXEvent
import tornadofx.booleanProperty
import tornadofx.doubleProperty
import tornadofx.objectProperty

class SyncSoundBitesViewModel : AppViewModel() {
    val finished: BooleanProperty = booleanProperty(false)
    val progress: DoubleProperty = doubleProperty()
    val tree: ObjectProperty<TreeItem<SoundBiteTreeModel>> = objectProperty()

    override fun onReady() {
        updateSounds()
    }

    private fun updateSounds() {
        progress.set(INDETERMINATE_PROGRESS)
        coroutineScope.launch {
            val result = SoundBites.synchronise(progress::set)
            if (result == null) {
                withContext(Main) {
                    showErrorDialog()
                }
                return@launch
            }
            val root = TreeItem<SoundBiteTreeModel>().apply {
                children += SoundBiteTreeItem(getString("label_new_sounds"), result.newSounds)
                children += SoundBiteTreeItem(getString("label_changed_sounds"), result.changedSounds)
                children += SoundBiteTreeItem(getString("label_deleted_sounds"), result.deletedSounds)
                children += SoundBiteTreeItem(getString("label_failed_sounds"), result.failedSounds)
            }
            withContext(Main) {
                tree.set(root)
                finished.set(true)
                SoundBites.checkForInvalidSounds()
            }
        }
    }

    private fun showErrorDialog() {
        error(getString("title_unknown_error"), getString("content_update_check_failed"), RETRY_BUTTON, ButtonType.CANCEL) {
            if (it === RETRY_BUTTON) {
                updateSounds()
            } else {
                fire(CloseEvent())
            }
        }
    }

    class CloseEvent : FXEvent()
}