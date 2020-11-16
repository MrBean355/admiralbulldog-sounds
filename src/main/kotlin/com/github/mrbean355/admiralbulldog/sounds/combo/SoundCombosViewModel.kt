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
import tornadofx.*

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