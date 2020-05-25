package com.github.mrbean355.admiralbulldog.sounds

import com.github.mrbean355.admiralbulldog.arch.AppViewModel
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.events.SOUND_EVENT_TYPES
import com.github.mrbean355.admiralbulldog.events.SoundTrigger
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import javafx.beans.property.ObjectProperty
import javafx.beans.property.StringProperty
import javafx.scene.paint.Color
import tornadofx.doubleProperty
import tornadofx.objectProperty
import tornadofx.onChange
import tornadofx.stringProperty
import kotlin.reflect.KClass

class ViewSoundTriggersViewModel : AppViewModel() {
    private val text: Map<KClass<out SoundTrigger>, StringProperty> = SOUND_EVENT_TYPES.associateWith { stringProperty() }
    private val colours: Map<KClass<out SoundTrigger>, ObjectProperty<Color>> = SOUND_EVENT_TYPES.associateWith { objectProperty<Color>() }

    val volumeProperty = doubleProperty(ConfigPersistence.getVolume())

    init {
        volumeProperty.onChange(ConfigPersistence::setVolume)
        refresh()
    }

    fun textProperty(type: KClass<out SoundTrigger>): StringProperty = text.getValue(type)

    fun textColourProperty(type: KClass<out SoundTrigger>): ObjectProperty<Color> = colours.getValue(type)

    fun onConfigureClicked(type: KClass<out SoundTrigger>) {
        find<ConfigureSoundTriggerScreen>(params = ConfigureSoundTriggerScreen.params(type))
                .openModal(block = true, resizable = false)

        refresh()
    }

    private fun refresh() {
        text.forEach { (type, prop) ->
            prop.set(textFor(type))
        }
        colours.forEach { (type, prop) ->
            prop.set(colourFor(type))
        }
    }

    private fun textFor(type: KClass<out SoundTrigger>): String {
        return if (ConfigPersistence.isSoundEventEnabled(type)) {
            type.friendlyName
        } else {
            getString("label_trigger_disabled", type.friendlyName)
        }
    }

    private fun colourFor(type: KClass<out SoundTrigger>): Color {
        return if (ConfigPersistence.isSoundEventEnabled(type)) Color.BLACK else Color.GRAY
    }
}
