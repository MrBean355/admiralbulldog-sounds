package com.github.mrbean355.admiralbulldog.sounds.manager

import com.github.mrbean355.admiralbulldog.assets.SoundBite
import com.github.mrbean355.admiralbulldog.assets.SoundBites
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import com.github.mrbean355.admiralbulldog.triggers.SoundTriggerType
import javafx.beans.property.BooleanProperty
import javafx.beans.property.StringProperty
import javafx.collections.ObservableList
import tornadofx.ViewModel
import tornadofx.booleanProperty
import tornadofx.onChange
import tornadofx.stringProperty
import tornadofx.toObservable

class SoundManagerViewModel : ViewModel() {
    val searchQuery: StringProperty = stringProperty()
    val items: ObservableList<SoundBite> = SoundBites.getAll().toObservable()

    init {
        searchQuery.onChange { text ->
            val query = text.orEmpty().trim()
            items.setAll(SoundBites.getAll().filter { it.name.contains(query, ignoreCase = true) })
        }
    }

    fun getCellProperty(type: SoundTriggerType, index: Int): BooleanProperty {
        val sound = items[index]
        return booleanProperty(ConfigPersistence.isSoundSelectedForType(type, sound)).apply {
            onChange { ConfigPersistence.setSoundSelectedForType(type, sound, it) }
        }
    }
}