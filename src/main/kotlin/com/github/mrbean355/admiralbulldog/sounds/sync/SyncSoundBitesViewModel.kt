package com.github.mrbean355.admiralbulldog.sounds.sync

import com.github.mrbean355.admiralbulldog.arch.AppViewModel
import com.github.mrbean355.admiralbulldog.assets.SoundBites
import com.github.mrbean355.admiralbulldog.common.*
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import javafx.beans.property.BooleanProperty
import javafx.beans.property.DoubleProperty
import javafx.beans.property.ObjectProperty
import javafx.scene.control.ButtonType
import javafx.scene.control.ProgressIndicator.INDETERMINATE_PROGRESS
import javafx.scene.control.TreeItem
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
        updateSounds()
    }

    private fun updateSounds() {
        progress.set(INDETERMINATE_PROGRESS)
        viewModelScope.launch {
            val result = SoundBites.synchronise(progress::set)
            if (result == null) {
                showErrorDialog()
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
        showError(getString("title_unknown_error"), getString("content_update_check_failed"), RETRY_BUTTON, ButtonType.CANCEL) {
            if (it === RETRY_BUTTON) {
                updateSounds()
            } else {
                fire(CloseEvent())
            }
        }
    }

    class CloseEvent : FXEvent()
}