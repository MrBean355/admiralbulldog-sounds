package com.github.mrbean355.admiralbulldog.sounds

import com.github.mrbean355.admiralbulldog.assets.SoundBite
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import com.github.mrbean355.admiralbulldog.triggers.SoundTriggerType
import tornadofx.Fragment

class ChooseSoundFilesScreen : Fragment() {
    private val type by param<SoundTriggerType>()
    private val tracker = SoundBiteTracker(ConfigPersistence.getSoundsForType(type))

    override val root = tracker.createUi(this, ::onSaveClicked)

    init {
        title = type.friendlyName
    }

    private fun onSaveClicked(selection: List<SoundBite>) {
        ConfigPersistence.saveSoundsForType(type, selection)
        close()
    }

    companion object {

        fun params(type: SoundTriggerType): Map<String, Any?> {
            return mapOf("type" to type)
        }
    }
}