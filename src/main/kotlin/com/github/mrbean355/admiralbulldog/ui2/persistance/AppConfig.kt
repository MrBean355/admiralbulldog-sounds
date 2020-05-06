package com.github.mrbean355.admiralbulldog.ui2.persistance

import com.github.mrbean355.admiralbulldog.ui2.SoundBite
import com.github.mrbean355.admiralbulldog.ui2.triggers.SoundTrigger
import com.github.mrbean355.admiralbulldog.ui2.update.UpdateFrequency
import com.google.gson.GsonBuilder
import javafx.beans.property.BooleanProperty
import javafx.beans.property.DoubleProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import java.io.File
import java.util.Date

object AppConfig {
    private val file = File("config2.json")
    private val gson = GsonBuilder().setPrettyPrinting().create()
    private val config: Config

    private var volumeProperty: DoubleProperty? = null
    private var updateFrequencyProperty: ObjectProperty<UpdateFrequency>? = null
    private var discordBotEnabledProperty: BooleanProperty? = null
    private var discordMagicNumberProperty: StringProperty? = null
    private val enabledProperties = mutableMapOf<String, BooleanProperty>()
    private val chanceProperties = mutableMapOf<String, DoubleProperty>()
    private val minRateProperties = mutableMapOf<String, DoubleProperty>()
    private val maxRateProperties = mutableMapOf<String, DoubleProperty>()
    private val playThroughDiscordProperties = mutableMapOf<String, BooleanProperty>()
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

    fun getLastUpdateCheck(): Long {
        return config.lastUpdateCheck
    }

    fun setLastUpdateCheckToNow() {
        config.lastUpdateCheck = Date().time
        save()
    }

    fun updateFrequencyProperty(): ObjectProperty<UpdateFrequency> {
        var property = updateFrequencyProperty
        if (property == null) {
            property = objectProperty(config.updateFrequency) {
                config.updateFrequency = it
            }
            updateFrequencyProperty = property
        }
        return property
    }

    fun discordBotEnabledProperty(): BooleanProperty {
        var property = discordBotEnabledProperty
        if (property == null) {
            property = booleanProperty(config.discordBotEnabled) {
                config.discordBotEnabled = it
            }
            discordBotEnabledProperty = property
        }
        return property
    }

    fun discordMagicNumberProperty(): StringProperty {
        var property = discordMagicNumberProperty
        if (property == null) {
            property = stringProperty(config.discordMagicNumber) {
                config.discordMagicNumber = it
            }
            discordMagicNumberProperty = property
        }
        return property
    }

    fun triggerEnabledProperty(trigger: SoundTrigger): BooleanProperty {
        return enabledProperties.getOrPut(trigger.key) {
            booleanProperty(trigger.load().enabled) {
                trigger.load().enabled = it
            }
        }
    }

    fun triggerChanceProperty(trigger: SoundTrigger): DoubleProperty {
        return chanceProperties.getOrPut(trigger.key) {
            doubleProperty(trigger.load().chance) {
                trigger.load().chance = it
            }
        }
    }

    fun getTriggerSoundBites(trigger: SoundTrigger): Collection<SoundBite> {
        return trigger.load().bites.map {
            SoundBite.named(it)
        }
    }

    fun triggerMinRateProperty(trigger: SoundTrigger): DoubleProperty {
        return minRateProperties.getOrPut(trigger.key) {
            doubleProperty(trigger.load().minRate) {
                trigger.load().minRate = it
            }
        }
    }

    fun triggerMaxRateProperty(trigger: SoundTrigger): DoubleProperty {
        return maxRateProperties.getOrPut(trigger.key) {
            doubleProperty(trigger.load().maxRate) {
                trigger.load().maxRate = it
            }
        }
    }

    fun playThroughDiscordProperty(trigger: SoundTrigger): BooleanProperty {
        return playThroughDiscordProperties.getOrPut(trigger.key) {
            booleanProperty(trigger.load().playThroughDiscord) {
                trigger.load().playThroughDiscord = it
            }
        }
    }

    fun triggerSoundBitesProperty(trigger: SoundTrigger, bite: SoundBite): BooleanProperty {
        val props = soundBiteEnabledProperties.getOrPut(trigger.key) { mutableMapOf() }
        return props.getOrPut(bite.name) {
            booleanProperty(bite.name in trigger.load().bites) {
                if (it) trigger.load().bites += bite.name
                else trigger.load().bites -= bite.name
            }
        }
    }

    private fun SoundTrigger.load(): Trigger {
        var save = false
        val e = config.triggers.getOrPut(key) {
            save = true
            Trigger()
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

    private fun stringProperty(initialValue: String, onChanged: (String) -> Unit): StringProperty {
        return object : SimpleStringProperty(initialValue) {
            override fun invalidated() {
                onChanged(value)
                save()
            }
        }
    }

    private fun <T> objectProperty(initialValue: T, onChanged: (T) -> Unit): ObjectProperty<T> {
        return object : SimpleObjectProperty<T>(initialValue) {
            override fun invalidated() {
                onChanged(value)
                save()
            }
        }
    }

    private inline val SoundTrigger.key: String
        get() = this::class.java.simpleName

    private class Config(
            var volume: Double = 20.0,
            var lastUpdateCheck: Long = 0,
            var updateFrequency: UpdateFrequency = UpdateFrequency.WEEKLY,
            var discordBotEnabled: Boolean = false,
            var discordMagicNumber: String = "",
            val triggers: MutableMap<String, Trigger> = mutableMapOf()
    )

    private class Trigger(
            var enabled: Boolean = false,
            var chance: Double = 100.0,
            var minRate: Double = 100.0,
            var maxRate: Double = 100.0,
            var playThroughDiscord: Boolean = false,
            val bites: MutableSet<String> = mutableSetOf()
    )
}