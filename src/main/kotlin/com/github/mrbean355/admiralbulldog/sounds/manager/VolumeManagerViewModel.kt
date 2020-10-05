package com.github.mrbean355.admiralbulldog.sounds.manager

import com.github.mrbean355.admiralbulldog.arch.AppViewModel
import com.github.mrbean355.admiralbulldog.common.Volume
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.common.showInformation
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import javafx.beans.binding.Binding
import javafx.beans.property.ObjectProperty
import javafx.collections.ObservableList
import tornadofx.*

class VolumeManagerViewModel : AppViewModel() {
    val items: ObservableList<Volume> = observableListOf()
    val selection: ObjectProperty<Volume> = objectProperty()
    val hasSelection: Binding<Boolean> = selection.isNotNull

    override fun onReady() {
        refreshItems()
    }

    fun onHelpClicked() {
        showInformation(getString("header_about_volume_manager"), getString("content_about_volume_manager"))
    }

    fun onRemoveVolumeClicked() {
        ConfigPersistence.removeSoundBiteVolume(selection.get().name)
        refreshItems()
    }

    fun onAddVolumeClicked() {
        find<ChooseVolumeScreen>().openModal(block = true, resizable = false)
        refreshItems()
    }

    fun onListDoubleClicked() {
        val name = selection.get()?.name ?: return
        find<ChooseVolumeScreen>(ChooseVolumeScreen.params(name)).openModal(block = true, resizable = false)
        refreshItems()
    }

    private fun refreshItems() {
        items.setAll(ConfigPersistence.getSoundBiteVolumes().map { Volume(it.key, it.value) }.sortedBy { it.name })
    }
}