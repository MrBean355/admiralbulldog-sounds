package com.github.mrbean355.admiralbulldog.ui2.persistance

import com.github.mrbean355.admiralbulldog.ui2.SoundBite
import com.github.mrbean355.admiralbulldog.ui2.events.SoundEvent
import com.google.gson.GsonBuilder
import javafx.beans.property.BooleanProperty
import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import java.io.File

object AppConfig {
    private val file = File("config2.json")
    private val gson = GsonBuilder().setPrettyPrinting().create()
    private val config: Config

    private var volumeProperty: DoubleProperty? = null
    private val enabledProperties = mutableMapOf<String, BooleanProperty>()
    private val chanceProperties = mutableMapOf<String, DoubleProperty>()
    private val minRateProperties = mutableMapOf<String, DoubleProperty>()
    private val maxRateProperties = mutableMapOf<String, DoubleProperty>()
    private val soundBiteEnabledProperties = mutableMapOf<String, MutableMap<String, BooleanProperty>>()

    init {
        config = if (file.exists()) {
            gson.fromJson(file.readText(), Config::class.java)
        } else {
            Config().also {
                file.writeText(gson.toJson(it))
            }
        }
    }

    fun volumeProperty(): DoubleProperty {
        var property = volumeProperty
        if (property == null) {
            property = doubleProperty(config.volume) {
                config.volume = it
            }
            volumeProperty = property
        }
        return property
    }

    fun eventEnabledProperty(event: SoundEvent): BooleanProperty {
        return enabledProperties.getOrPut(event.key) {
            booleanProperty(getEvent(event).enabled) {
                getEvent(event).enabled = it
            }
        }
    }

    fun eventChanceProperty(event: SoundEvent): DoubleProperty {
        return chanceProperties.getOrPut(event.key) {
            doubleProperty(getEvent(event).chance) {
                getEvent(event).chance = it
            }
        }
    }

    fun eventMinRateProperty(event: SoundEvent): DoubleProperty {
        return minRateProperties.getOrPut(event.key) {
            doubleProperty(getEvent(event).minRate) {
                getEvent(event).minRate = it
            }
        }
    }

    fun eventMaxRateProperty(event: SoundEvent): DoubleProperty {
        return maxRateProperties.getOrPut(event.key) {
            doubleProperty(getEvent(event).maxRate) {
                getEvent(event).maxRate = it
            }
        }
    }

    fun eventSoundBitesProperty(event: SoundEvent, bite: SoundBite): BooleanProperty {
        val props = soundBiteEnabledProperties.getOrPut(event.key) { mutableMapOf() }
        return props.getOrPut(bite.name) {
            booleanProperty(bite.name in getEvent(event).bites) {
                if (it) getEvent(event).bites += bite.name
                else getEvent(event).bites -= bite.name
            }
        }
    }

    private fun getEvent(event: SoundEvent): Event {
        var save = false
        val e = config.events.getOrPut(event.key) {
            save = true
            Event()
        }
        if (save) {
            save()
        }
        return e
    }

    private fun save() {
        file.writeText(gson.toJson(config))
    }

    private fun booleanProperty(initialValue: Boolean, onChanged: (Boolean) -> Unit): BooleanProperty {
        return object : SimpleBooleanProperty(initialValue) {
            override fun invalidated() {
                onChanged(value)
                save()
            }
        }
    }

    private fun doubleProperty(initialValue: Double, onChanged: (Double) -> Unit): DoubleProperty {
        return object : SimpleDoubleProperty(initialValue) {
            override fun invalidated() {
                onChanged(value)
                save()
            }
        }
    }

    private inline val SoundEvent.key: String
        get() = this::class.java.simpleName

    private class Config(
            var volume: Double = 20.0,
            val events: MutableMap<String, Event> = mutableMapOf()
    )

    private class Event(
            var enabled: Boolean = false,
            var chance: Double = 100.0,
            var minRate: Double = 100.0,
            var maxRate: Double = 100.0,
            val bites: MutableSet<String> = mutableSetOf()
    )
}