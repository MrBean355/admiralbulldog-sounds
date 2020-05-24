package com.github.mrbean355.admiralbulldog.sounds

import com.github.mrbean355.admiralbulldog.arch.AppViewModel
import com.github.mrbean355.admiralbulldog.events.OnHeal
import com.github.mrbean355.admiralbulldog.events.SoundEvent
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import javafx.beans.binding.BooleanBinding
import javafx.beans.property.BooleanProperty
import javafx.beans.property.DoubleProperty
import javafx.beans.property.StringProperty
import tornadofx.booleanProperty
import tornadofx.doubleProperty
import tornadofx.onChange
import tornadofx.stringProperty
import kotlin.reflect.KClass

class ConfigureSoundTriggerViewModel : AppViewModel() {
    private val type: KClass<out SoundEvent> by param()

    val title: StringProperty = stringProperty(type.friendlyName)
    val description: StringProperty = stringProperty(type.description)
    val enabled: BooleanProperty = booleanProperty(ConfigPersistence.isSoundEventEnabled(type))
    val showSmartChance: BooleanProperty = booleanProperty(type == OnHeal::class)
    val useSmartChance: BooleanProperty = booleanProperty(ConfigPersistence.isUsingHealSmartChance())
    val enableChanceSlider: BooleanBinding = showSmartChance.not().or(useSmartChance.not())
    val chance: DoubleProperty = doubleProperty(ConfigPersistence.getSoundEventChance(type))
    val minRate: DoubleProperty = doubleProperty(ConfigPersistence.getSoundEventMinRate(type))
    val maxRate: DoubleProperty = doubleProperty(ConfigPersistence.getSoundEventMaxRate(type))
    val soundBiteCount = stringProperty(ConfigPersistence.getSoundsForType(type).size.toString())

    init {
        enabled.onChange { ConfigPersistence.toggleSoundEvent(type, it) }
        useSmartChance.onChange { ConfigPersistence.setIsUsingHealSmartChance(it) }
        chance.onChange { ConfigPersistence.setSoundEventChance(type, it) }
        minRate.onChange {
            ConfigPersistence.setSoundEventMinRate(type, it)
            if (it > maxRate.get()) {
                maxRate.set(it)
            }
        }
        maxRate.onChange {
            ConfigPersistence.setSoundEventMaxRate(type, it)
            if (it < minRate.get()) {
                minRate.set(it)
            }
        }
    }

    fun onChooseSoundsClicked() {
        find<ChooseSoundFilesScreen>(ChooseSoundFilesScreen.params(type))
                .openModal(block = true, resizable = false)
        soundBiteCount.set(ConfigPersistence.getSoundsForType(type).size.toString())
    }
}