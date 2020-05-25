package com.github.mrbean355.admiralbulldog.sounds

import com.github.mrbean355.admiralbulldog.arch.AppViewModel
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import com.github.mrbean355.admiralbulldog.triggers.SOUND_TRIGGER_TYPES
import com.github.mrbean355.admiralbulldog.triggers.SoundTriggerType
import javafx.beans.property.ObjectProperty
import javafx.beans.property.StringProperty
import javafx.scene.paint.Color
import tornadofx.objectProperty
import tornadofx.stringProperty

class ViewSoundTriggersViewModel : AppViewModel() {
    private val text: Map<SoundTriggerType, StringProperty> = SOUND_TRIGGER_TYPES.associateWith { stringProperty() }
    private val colours: Map<SoundTriggerType, ObjectProperty<Color>> = SOUND_TRIGGER_TYPES.associateWith { objectProperty<Color>() }

    init {
        refresh()
    }

    fun textProperty(type: SoundTriggerType): StringProperty = text.getValue(type)

    fun textColourProperty(type: SoundTriggerType): ObjectProperty<Color> = colours.getValue(type)

    fun onConfigureClicked(type: SoundTriggerType) {
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

    private fun textFor(type: SoundTriggerType): String {
        return if (ConfigPersistence.isSoundTriggerEnabled(type)) {
            type.friendlyName
        } else {
            getString("label_trigger_disabled", type.friendlyName)
        }
    }

    private fun colourFor(type: SoundTriggerType): Color {
        return if (ConfigPersistence.isSoundTriggerEnabled(type)) Color.BLACK else Color.GRAY
    }
}
