package com.github.mrbean355.admiralbulldog.ui2.sounds

import com.github.mrbean355.admiralbulldog.ui2.persistance.AppConfig
import com.github.mrbean355.admiralbulldog.ui2.triggers.SoundTrigger
import javafx.beans.property.BooleanProperty
import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import tornadofx.ViewModel

class ConfigureSoundTriggerViewModel : ViewModel() {
    private val soundTrigger by param<SoundTrigger>()

    val title: StringProperty = SimpleStringProperty(soundTrigger.name)
    val description: StringProperty = SimpleStringProperty(soundTrigger.description)
    val enabled: BooleanProperty = AppConfig.triggerEnabledProperty(soundTrigger)
    val chance: DoubleProperty = AppConfig.triggerChanceProperty(soundTrigger)
    val minRate: DoubleProperty = AppConfig.triggerMinRateProperty(soundTrigger)
    val maxRate: DoubleProperty = AppConfig.triggerMaxRateProperty(soundTrigger)
}