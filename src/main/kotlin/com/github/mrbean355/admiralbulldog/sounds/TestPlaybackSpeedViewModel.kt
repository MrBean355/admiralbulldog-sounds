package com.github.mrbean355.admiralbulldog.sounds

import com.github.mrbean355.admiralbulldog.arch.AppViewModel
import com.github.mrbean355.admiralbulldog.assets.SoundBite
import com.github.mrbean355.admiralbulldog.assets.SoundBites
import javafx.beans.property.StringProperty
import javafx.collections.ObservableList
import tornadofx.observableListOf
import tornadofx.onChange
import tornadofx.stringProperty

class TestPlaybackSpeedViewModel : AppViewModel() {
    val searchQuery: StringProperty = stringProperty()
    val displayItems: ObservableList<SoundBite> = observableListOf(SoundBites.getAll())

    init {
        searchQuery.onChange {
            val query = it.orEmpty().trim()
            val newItems = SoundBites.getAll().filter {
                it.name.contains(query, ignoreCase = true)
            }
            displayItems.setAll(newItems)
        }
    }
}