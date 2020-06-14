package com.github.mrbean355.admiralbulldog.sounds.manager

import com.github.mrbean355.admiralbulldog.arch.AppViewModel
import com.github.mrbean355.admiralbulldog.assets.SoundBite
import com.github.mrbean355.admiralbulldog.assets.SoundBites
import com.github.mrbean355.admiralbulldog.common.DEFAULT_INDIVIDUAL_VOLUME
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import javafx.beans.binding.Binding
import javafx.beans.property.IntegerProperty
import javafx.beans.property.ObjectProperty
import tornadofx.intProperty
import tornadofx.objectProperty
import tornadofx.onChange
import tornadofx.runLater
import tornadofx.stringProperty

class ChooseVolumeViewModel : AppViewModel() {
    private var lastText = ""
    private val soundBite: ObjectProperty<SoundBite> = objectProperty()

    val query = stringProperty((params["name"] as? String).orEmpty())
    val hasSoundBite: Binding<Boolean> = soundBite.isNotNull
    val volume: IntegerProperty = intProperty(DEFAULT_INDIVIDUAL_VOLUME)

    init {
        query.onChange {
            onQueryChanged(it.orEmpty())
        }
        query.get().also {
            // Load initial text & volume when editing.
            if (it.isNotEmpty()) {
                onQueryChanged(it)
                volume.set(ConfigPersistence.getSoundBiteVolume(it) ?: DEFAULT_INDIVIDUAL_VOLUME)
            }
        }
    }

    fun onPlayClicked() {
        soundBite.get().play(volume = volume.get())
    }

    fun onDoneClicked() {
        ConfigPersistence.addSoundBiteVolume(soundBite.get().name, volume.get())
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