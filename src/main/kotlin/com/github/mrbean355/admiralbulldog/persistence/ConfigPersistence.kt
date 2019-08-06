package com.github.mrbean355.admiralbulldog.persistence

import com.github.mrbean355.admiralbulldog.bytes.SoundByte
import com.google.gson.GsonBuilder
import java.io.File
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberProperties

object ConfigPersistence {
    private val gson = GsonBuilder().setPrettyPrinting().create()
    private lateinit var config: Config

    /** Load config from file into memory. */
    fun initialise() {
        val file = File("config_new.json")
        if (!file.exists()) {
            file.createNewFile()
            file.writeText("{}")
        }
        config = gson.fromJson(file.readText(), Config::class.java)
    }

    fun isSoundByteEnabled(type: KClass<out SoundByte>): Boolean {
        return reflect(type)?.enabled ?: false
    }

    fun toggleSoundByte(type: KClass<out SoundByte>, enabled: Boolean) {
        reflect(type)?.enabled = enabled
        save()
    }

    fun getSoundsForType(type: KClass<out SoundByte>): List<String> {
        return reflect(type)?.sounds.orEmpty()
    }

    fun saveSoundsForType(type: KClass<out SoundByte>, selection: List<String>) {
        reflect(type)?.sounds = selection
        save()
    }

    private fun reflect(type: KClass<out SoundByte>): Toggle? {
        val prop = Config::class.declaredMemberProperties.firstOrNull { it.name == type.simpleName }
                ?: return null

        return prop.get(config) as Toggle
    }

    private fun save() {
        val file = File("config_new.json")
        if (!file.exists()) {
            file.createNewFile()
        }
        file.writeText(gson.toJson(config))
    }

    data class Config(
            val OnBountyRunesSpawn: Toggle,
            val OnDeath: Toggle,
            val OnDefeat: Toggle,
            val OnHeal: Toggle,
            val OnKill: Toggle,
            val OnMatchStart: Toggle,
            val OnMidasReady: Toggle,
            val OnSmoked: Toggle,
            val OnVictory: Toggle,
            val Periodically: Toggle)

    data class Toggle(
            var enabled: Boolean,
            var sounds: List<String>)
}