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

package com.github.mrbean355.admiralbulldog.sounds.combo

import com.github.mrbean355.admiralbulldog.arch.AppViewModel
import com.github.mrbean355.admiralbulldog.assets.ComboSoundBite
import com.github.mrbean355.admiralbulldog.assets.SoundBite
import com.github.mrbean355.admiralbulldog.assets.SoundBites
import com.github.mrbean355.admiralbulldog.assets.playAll
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.common.showError
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import javafx.beans.binding.Binding
import javafx.beans.property.ObjectProperty
import kotlinx.coroutines.launch
import tornadofx.and
import tornadofx.ge
import tornadofx.objectProperty
import tornadofx.observableListOf
import tornadofx.onChange
import tornadofx.runLater
import tornadofx.sizeProperty
import tornadofx.stringProperty

class CreateSoundComboViewModel : AppViewModel() {
    private var lastText = ""
    private val soundBite: ObjectProperty<SoundBite> = objectProperty()

    private val originalSound = (params["soundBite"] as? ComboSoundBite)
    val name = stringProperty(originalSound?.name.orEmpty())
    val query = stringProperty()
    val hasSoundBite: Binding<Boolean> = soundBite.isNotNull
    val items = observableListOf<SoundBite>()
    val canSave = name.length().ge(1) and items.sizeProperty.ge(2)

    init {
        query.onChange {
            onQueryChanged(it.orEmpty())
        }
        if (originalSound != null) {
            items.setAll(originalSound.getSoundBites())
        }
    }

    fun onAddClicked() {
        items += soundBite.get()
        query.set("")
    }

    fun onRemoveClicked(index: Int) {
        items.removeAt(index)
    }

    fun onPlayClicked() {
        viewModelScope.launch {
            items.playAll()
        }
    }

    fun onSaveClicked(): Boolean {
        val newName = name.get()
        if (originalSound == null) {
            if (ConfigPersistence.hasSoundCombo(newName)) {
                showError(getString("header_sound_combo_exists"), getString("content_sound_combo_exists", newName))
                return false
            }
        } else {
            if (newName != originalSound.name && ConfigPersistence.hasSoundCombo(newName)) {
                showError(getString("header_sound_combo_exists"), getString("content_sound_combo_exists", newName))
                return false
            }
            ConfigPersistence.removeSoundCombo(originalSound)
        }
        ConfigPersistence.saveSoundCombo(newName, items)
        SoundBites.refreshCombos()
        return true
    }

    private fun onQueryChanged(text: String) {
        val isDeleting = text.length < lastText.length
        if (!isDeleting) {
            val match = SoundBites.getSingleSoundBites().singleOrNull { it.name.startsWith(text, ignoreCase = true) }
            if (match != null) {
                runLater { query.set(match.name) }
            }
        }
        soundBite.set(SoundBites.getSingleSoundBites().find { it.name == text })
        lastText = text
    }
}