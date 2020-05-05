package com.github.mrbean355.admiralbulldog.ui2.sounds

import com.github.mrbean355.admiralbulldog.ui2.SoundBite
import com.github.mrbean355.admiralbulldog.ui2.persistance.AppConfig
import com.github.mrbean355.admiralbulldog.ui2.triggers.SoundTrigger
import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import tornadofx.ViewModel
import tornadofx.onChange

class ChooseSoundBitesViewModel : ViewModel() {
    private val soundTrigger by param<SoundTrigger>()
    private val allSoundBites = SoundBite.getAll().sortedWith(Comparator { lhs, rhs ->
        val lhsSelected = AppConfig.triggerSoundBitesProperty(soundTrigger, lhs).value
        val rhsSelected = AppConfig.triggerSoundBitesProperty(soundTrigger, rhs).value
        when {
            lhsSelected == rhsSelected -> lhs.name.compareTo(rhs.name, ignoreCase = true)
            lhsSelected -> -1
            else -> 1
        }
    })

    val searchQuery: StringProperty = SimpleStringProperty()
    val filteredSoundBites: ObservableList<SoundBite> = FXCollections.observableArrayList(allSoundBites)

    init {
        searchQuery.onChange { query ->
            val newItems = if (query.isNullOrBlank()) {
                allSoundBites
            } else {
                val trimmed = query.trim().toLowerCase()
                allSoundBites.filter { it.name.contains(trimmed) }
            }
            filteredSoundBites.setAll(newItems)
        }
    }

    fun getEnabledProperty(soundBite: SoundBite): BooleanProperty {
        return AppConfig.triggerSoundBitesProperty(soundTrigger, soundBite)
    }

    fun onPlayButtonClicked(soundBite: SoundBite) {
        soundBite.play()
    }
}
