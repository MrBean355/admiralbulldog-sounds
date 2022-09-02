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

package com.github.mrbean355.admiralbulldog.sounds.combo

import com.github.mrbean355.admiralbulldog.arch.AppViewModel
import com.github.mrbean355.admiralbulldog.assets.ComboSoundBite
import com.github.mrbean355.admiralbulldog.assets.SoundBites
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.common.showInformation
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import javafx.beans.property.BooleanProperty
import javafx.beans.property.ObjectProperty
import javafx.collections.ObservableList
import tornadofx.booleanProperty
import tornadofx.objectProperty
import tornadofx.observableListOf

class SoundCombosViewModel : AppViewModel() {
    val items: ObservableList<ComboSoundBite> = observableListOf()
    val selection: ObjectProperty<ComboSoundBite> = objectProperty()
    val hasSelection: BooleanProperty = booleanProperty(true)

    override fun onReady() {
        refreshItems()
    }

    fun onHelpClicked() {
        showInformation(getString("header_about_sound_combos"), getString("content_about_sound_combos"))
    }

    fun onRemoveClicked() {
        ConfigPersistence.removeSoundCombo(selection.get())
        SoundBites.refreshCombos()
        refreshItems()
    }

    fun onAddClicked() {
        find<CreateSoundComboScreen>().openModal(block = true, resizable = false)
        refreshItems()
    }

    fun onListDoubleClicked() {
        val name = selection.get() ?: return
        find<CreateSoundComboScreen>(CreateSoundComboScreen.params(name)).openModal(block = true, resizable = false)
        refreshItems()
    }

    private fun refreshItems() {
        items.setAll(SoundBites.getComboSoundBites().sortedBy { it.name })
    }
}