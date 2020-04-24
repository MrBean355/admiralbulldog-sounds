package com.github.mrbean355.admiralbulldog.ui2.persistance

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

    private val enabledProperties = mutableMapOf<String, BooleanProperty>()
    private val chanceProperties = mutableMapOf<String, DoubleProperty>()
    private val minRateProperties = mutableMapOf<String, DoubleProperty>()
    private val maxRateProperties = mutableMapOf<String, DoubleProperty>()

    init {
        config = if (file.exists()) {
            gson.fromJson(file.readText(), Config::class.java)
        } else {
            Config().also {
                file.writeText(gson.toJson(it))
            }
        }
    }

    fun eventEnabledProperty(event: SoundEvent): BooleanProperty {
        return enabledProperties.getOrPut(event.key) {
            object : SimpleBooleanProperty(getEvent(event).enabled) {
                override fun invalidated() {
                    getEvent(event).enabled = value
                    save()
                }
            }
        }
    }

    fun eventChanceProperty(event: SoundEvent): DoubleProperty {
        return chanceProperties.getOrPut(event.key) {
            object : SimpleDoubleProperty(getEvent(event).chance) {
                override fun invalidated() {
                    getEvent(event).chance = value
                    save()
                }
            }
        }
    }

    fun eventMinRateProperty(event: SoundEvent): DoubleProperty {
        return minRateProperties.getOrPut(event.key) {
            object : SimpleDoubleProperty(getEvent(event).minRate) {
                override fun invalidated() {
                    getEvent(event).minRate = value
                    save()
                }
            }
        }
    }

    fun eventMaxRateProperty(event: SoundEvent): DoubleProperty {
        return maxRateProperties.getOrPut(event.key) {
            object : SimpleDoubleProperty(getEvent(event).maxRate) {
                override fun invalidated() {
                    getEvent(event).maxRate = value
                    save()
                }
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

    private inline val SoundEvent.key: String
        get() = this::class.java.simpleName

    private class Config(
            val events: MutableMap<String, Event> = mutableMapOf()
    )

    private class Event(
            var enabled: Boolean = false,
            var chance: Double = 100.0,
            var minRate: Double = 100.0,
            var maxRate: Double = 100.0
    )
}