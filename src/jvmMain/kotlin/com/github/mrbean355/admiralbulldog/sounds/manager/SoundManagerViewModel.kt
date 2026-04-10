package com.github.mrbean355.admiralbulldog.sounds.manager

import com.github.mrbean355.admiralbulldog.assets.SoundBite
import com.github.mrbean355.admiralbulldog.assets.SoundBites
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import com.github.mrbean355.admiralbulldog.sounds.combo.SoundCombosScreen
import com.github.mrbean355.admiralbulldog.triggers.SOUND_TRIGGER_TYPES
import com.github.mrbean355.admiralbulldog.triggers.SoundTriggerType
import com.github.mrbean355.admiralbulldog.ui.openScreen
import javafx.beans.property.BooleanProperty
import javafx.beans.property.StringProperty
import javafx.collections.ObservableList
import tornadofx.ViewModel
import tornadofx.booleanProperty
import tornadofx.observableListOf
import tornadofx.onChange
import tornadofx.stringProperty

class SoundManagerViewModel : ViewModel() {
    val searchQuery: StringProperty = stringProperty()
    val showPlaySounds: BooleanProperty = booleanProperty(true)
    val showCombos: BooleanProperty = booleanProperty(true)
    val showUsed: BooleanProperty = booleanProperty(true)
    val showUnused: BooleanProperty = booleanProperty(true)
    val items: ObservableList<SoundBite> = observableListOf()

    init {
        showPlaySounds.onChange { refreshDisplayedSoundBites() }
        showCombos.onChange { refreshDisplayedSoundBites() }
        showUsed.onChange { refreshDisplayedSoundBites() }
        showUnused.onChange { refreshDisplayedSoundBites() }
        searchQuery.onChange { refreshDisplayedSoundBites() }
        refreshDisplayedSoundBites()
    }

    fun getCellProperty(type: SoundTriggerType, index: Int): BooleanProperty {
        val sound = items[index]
        return booleanProperty(ConfigPersistence.isSoundSelectedForType(type, sound)).apply {
            onChange { ConfigPersistence.setSoundSelectedForType(type, sound, it) }
        }
    }

    fun onManageVolumesClicked() {
        openScreen<VolumeManagerScreen>()
    }

    fun onSoundCombosClicked() {
        openScreen<SoundCombosScreen>(block = true)
        refreshDisplayedSoundBites()
    }

    private fun refreshDisplayedSoundBites() {
        val newItems = mutableListOf<SoundBite>()
        if (showPlaySounds.value) {
            newItems += SoundBites.getSingleSoundBites()
        }
        if (showCombos.value) {
            newItems += SoundBites.getComboSoundBites()
        }
        if (!showUsed.value) {
            newItems.removeAll { it.isUsed() }
        }
        if (!showUnused.value) {
            newItems.removeAll { !it.isUsed() }
        }
        val query = searchQuery.value.orEmpty().trim()
        items.setAll(
            newItems
                .filter { it.name.contains(query, ignoreCase = true) }
                .sortedBy { it.name.lowercase() }
        )
    }

    private fun SoundBite.isUsed(): Boolean = SOUND_TRIGGER_TYPES.any {
        ConfigPersistence.isSoundSelectedForType(it, this)
    }
}