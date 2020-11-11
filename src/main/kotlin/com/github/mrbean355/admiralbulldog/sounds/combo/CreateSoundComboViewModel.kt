package com.github.mrbean355.admiralbulldog.sounds.combo

import com.github.mrbean355.admiralbulldog.arch.AppViewModel
import com.github.mrbean355.admiralbulldog.assets.SoundBite
import com.github.mrbean355.admiralbulldog.assets.SoundBites
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.common.showError
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import javafx.beans.binding.Binding
import javafx.beans.property.ObjectProperty
import tornadofx.*

class CreateSoundComboViewModel : AppViewModel() {
    private var lastText = ""
    private val soundBite: ObjectProperty<SoundBite> = objectProperty()

    private val originalName = (params["name"] as? String).orEmpty()
    val name = stringProperty(originalName)
    val query = stringProperty()
    val hasSoundBite: Binding<Boolean> = soundBite.isNotNull
    val items = observableListOf<SoundBite>()
    val canSave = name.length().ge(1) and items.sizeProperty.ge(2)

    init {
        query.onChange {
            onQueryChanged(it.orEmpty())
        }
        if (originalName.isNotEmpty()) {
            items.setAll(ConfigPersistence.findSoundCombo(originalName))
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
        // TODO: Play sounds.
    }

    fun onSaveClicked(): Boolean {
        val newName = name.get()
        if (originalName.isEmpty()) {
            if (ConfigPersistence.hasSoundCombo(newName)) {
                showError(getString("header_sound_combo_exists"), getString("content_sound_combo_exists", newName))
                return false
            }
        } else {
            if (newName != originalName && ConfigPersistence.hasSoundCombo(newName)) {
                showError(getString("header_sound_combo_exists"), getString("content_sound_combo_exists", newName))
                return false
            }
            ConfigPersistence.removeSoundCombo(originalName)
        }
        ConfigPersistence.saveSoundCombo(newName, items)
        return true
    }

    private fun onQueryChanged(text: String) {
        val isDeleting = text.length < lastText.length
        if (!isDeleting) {
            val match = SoundBites.getAll().singleOrNull { it.name.startsWith(text, ignoreCase = true) }
            if (match != null) {
                runLater { query.set(match.name) }
            }
        }
        soundBite.set(SoundBites.getAll().find { it.name == text })
        lastText = text
    }
}