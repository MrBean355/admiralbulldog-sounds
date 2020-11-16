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
import tornadofx.*

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