package com.github.mrbean355.admiralbulldog.sounds.combo

import com.github.mrbean355.admiralbulldog.arch.AppViewModel
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.common.showInformation
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import javafx.beans.property.BooleanProperty
import javafx.beans.property.StringProperty
import javafx.collections.ObservableList
import tornadofx.*

class SoundCombosViewModel : AppViewModel() {
    val items: ObservableList<String> = observableListOf()
    val selection: StringProperty = stringProperty()
    val hasSelection: BooleanProperty = booleanProperty(true)

    override fun onReady() {
        refreshItems()
    }

    fun onHelpClicked() {
        showInformation(getString("header_about_sound_combos"), getString("content_about_sound_combos"))
    }

    fun onRemoveClicked() {
        ConfigPersistence.removeSoundCombo(selection.get())
        refreshItems()
    }

    fun onAddClicked() {
        find<CreateSoundComboScreen>().openModal(block = true, resizable = false)
        refreshItems()
    }

    fun onPlayClicked(name: String) {
        // TODO: Play sounds.
    }

    fun onListDoubleClicked() {
        val name = selection.get() ?: return
        find<CreateSoundComboScreen>(CreateSoundComboScreen.params(name)).openModal(block = true, resizable = false)
        refreshItems()
    }

    private fun refreshItems() {
        items.setAll(ConfigPersistence.getSoundCombos().sorted())
    }
}