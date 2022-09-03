/*
 * Copyright 2022 Michael Johnston
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

import com.github.mrbean355.admiralbulldog.arch.AppViewModel
import com.github.mrbean355.admiralbulldog.common.Volume
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.common.showInformation
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import javafx.beans.binding.Binding
import javafx.beans.property.ObjectProperty
import javafx.collections.ObservableList
import tornadofx.objectProperty
import tornadofx.observableListOf

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