package com.github.mrbean355.admiralbulldog.persistence

import com.github.mrbean355.admiralbulldog.bytes.SOUND_BYTE_TYPES
import com.github.mrbean355.admiralbulldog.bytes.SoundByte
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.File
import kotlin.reflect.KClass

private const val FILE_NAME = "config.json"
private const val DEFAULTS_PATH = "defaults/%s.json"

/**
 * Facilitates saving & loading the configuration of the sound bytes from a file.
 * Sound bytes:
 * - have a list of possible sounds to play, configurable by the user at runtime
 * - can be enabled or disabled at runtime by the user
 */
object ConfigPersistence {
    private val gson = GsonBuilder().setPrettyPrinting().create()
    private lateinit var config: MutableMap<String, Toggle>

    /** Load config from file into memory. */
    fun initialise() {
        val file = File(FILE_NAME)
        if (!file.exists()) {
            file.createNewFile()
            config = loadAllDefaults().toMutableMap()
            save()
        } else {
            val typeToken = TypeToken.getParameterized(Map::class.java, String::class.java, Toggle::class.java)
            config = gson.fromJson(file.readText(), typeToken.type)
        }
        checkConfig()
    }

    /** @return `true` if the sound byte is enabled; `false` otherwise. */
    fun isSoundByteEnabled(type: KClass<out SoundByte>): Boolean {
        return config[type.simpleName]!!.enabled
    }

    /** Enable or disable a sound byte. */
    fun toggleSoundByte(type: KClass<out SoundByte>, enabled: Boolean) {
        config[type.simpleName]!!.enabled = enabled
        save()
    }

    /** @return all selected sounds for a sound byte if it's enabled; empty list otherwise. */
    fun getSoundsForType(type: KClass<out SoundByte>): List<String> {
        val toggle = config[type.simpleName]!!
        if (toggle.enabled) {
            return toggle.sounds
        }
        return emptyList()
    }

    /** Update the given sound byte's config to use the given sound `selection`. */
    fun saveSoundsForType(type: KClass<out SoundByte>, selection: List<String>) {
        config[type.simpleName]!!.sounds = selection
        save()
    }

    /** Save the current `config` map to file. */
    private fun save() {
        val file = File(FILE_NAME)
        if (!file.exists()) {
            file.createNewFile()
        }
        file.writeText(gson.toJson(config))
    }

    /** Load the default configs for all sound bytes. */
    private fun loadAllDefaults(): Map<String, Toggle> {
        return SOUND_BYTE_TYPES.associateWith {
            loadDefaults(it)
        }.mapKeys { it.key.simpleName!! }
    }

    /** Load the default config for a given sound byte `type`. */
    private fun loadDefaults(type: KClass<out SoundByte>): Toggle {
        val resource = javaClass.classLoader.getResource(DEFAULTS_PATH.format(type.simpleName))
                ?: return Toggle(false, emptyList())

        return gson.fromJson(resource.readText(), Toggle::class.java)
    }

    /** Checks the loaded `config` map, adding defaults for any missing sound bytes. */
    private fun checkConfig() {
        var changed = false
        SOUND_BYTE_TYPES.forEach {
            if (config[it.simpleName] == null) {
                config[it.simpleName!!] = loadDefaults(it)
                changed = true
            }
        }
        if (changed) {
            save()
        }
    }

    data class Toggle(var enabled: Boolean, var sounds: List<String>)
}