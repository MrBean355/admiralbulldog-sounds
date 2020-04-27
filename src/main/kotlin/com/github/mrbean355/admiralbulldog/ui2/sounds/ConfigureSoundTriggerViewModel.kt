package com.github.mrbean355.admiralbulldog.ui2.sounds

import com.github.mrbean355.admiralbulldog.ui2.persistance.AppConfig
import com.github.mrbean355.admiralbulldog.ui2.triggers.SoundTrigger
import javafx.beans.property.BooleanProperty
import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty

class ConfigureSoundTriggerViewModel(trigger: SoundTrigger) {
    val title: StringProperty = SimpleStringProperty(trigger.name)
    val description: StringProperty = SimpleStringProperty(trigger.description)
    val enabled: BooleanProperty = AppConfig.triggerEnabledProperty(trigger)
    val chance: DoubleProperty = AppConfig.triggerChanceProperty(trigger)
    val minRate: DoubleProperty = AppConfig.triggerMinRateProperty(trigger)
    val maxRate: DoubleProperty = AppConfig.triggerMaxRateProperty(trigger)
}