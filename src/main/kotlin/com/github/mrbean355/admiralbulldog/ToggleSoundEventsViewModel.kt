package com.github.mrbean355.admiralbulldog

import com.github.mrbean355.admiralbulldog.arch.AppViewModel
import com.github.mrbean355.admiralbulldog.events.SOUND_EVENT_TYPES
import com.github.mrbean355.admiralbulldog.events.SoundEvent
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import javafx.beans.property.BooleanProperty
import tornadofx.booleanProperty
import tornadofx.doubleProperty
import tornadofx.onChange
import kotlin.reflect.KClass

class ToggleSoundEventsViewModel : AppViewModel() {
    private val toggles: Map<KClass<out SoundEvent>, BooleanProperty>

    val volumeProperty = doubleProperty(ConfigPersistence.getVolume())

    init {
        toggles = SOUND_EVENT_TYPES.associateWith { type ->
            booleanProperty(ConfigPersistence.isSoundEventEnabled(type)).apply {
                onChange { ConfigPersistence.toggleSoundEvent(type, it) }
            }
        }
        volumeProperty.onChange(ConfigPersistence::setVolume)
    }

    fun enabledProperty(type: KClass<out SoundEvent>): BooleanProperty = toggles.getValue(type)

    fun onConfigureClicked(type: KClass<out SoundEvent>) {
        // TODO
    }
}
