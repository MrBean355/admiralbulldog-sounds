package com.github.mrbean355.admiralbulldog.ui2.sounds

import com.github.mrbean355.admiralbulldog.ui2.events.SoundEvent
import com.github.mrbean355.admiralbulldog.ui2.persistance.AppConfig
import javafx.beans.property.BooleanProperty
import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty

class ConfigureSoundEventViewModel(event: SoundEvent) {
    val title: StringProperty = SimpleStringProperty(event.name)
    val description: StringProperty = SimpleStringProperty(event.description)
    val enabled: BooleanProperty = AppConfig.eventEnabledProperty(event)
    val chance: DoubleProperty = AppConfig.eventChanceProperty(event)
    val minRate: DoubleProperty = AppConfig.eventMinRateProperty(event)
    val maxRate: DoubleProperty = AppConfig.eventMaxRateProperty(event)
}