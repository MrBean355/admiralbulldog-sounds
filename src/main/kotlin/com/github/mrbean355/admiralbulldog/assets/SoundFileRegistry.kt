package com.github.mrbean355.admiralbulldog.assets

import com.github.mrbean355.admiralbulldog.bytes.SOUND_BYTE_TYPES
import com.github.mrbean355.admiralbulldog.bytes.SoundByte
import com.google.gson.Gson
import java.io.File
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberProperties

private const val CONFIG_FILE_NAME = "config.json"
private const val DEFAULTS_FILE_NAME = "defaults.json"

/**
 * Loads a list of [SoundFile]s for each [SoundByte] from a config file.
 * This allows users to customise the sounds they hear for the different events.
 */
object SoundFileRegistry {
    private val map = mutableMapOf<KClass<out SoundByte>, List<SoundFile>>()

    fun initialise() {
        val file = File(CONFIG_FILE_NAME)
        // Create a config file if it doesn't exist, copying from the defaults file.
        if (!file.exists()) {
            file.createNewFile()
            val defaults = SoundFileRegistry::class.java.classLoader.getResource(DEFAULTS_FILE_NAME)?.readText()
                    ?: throw IllegalStateException("Unable to load $DEFAULTS_FILE_NAME")
            file.writeText(defaults)
        }
        val config = Gson().fromJson(file.readText(), SoundByteToggles::class.java)

        // Iterate through each existing SoundByte.
        // Try to look up a field in the config file with the same name.
        // If found, read SoundByte names from the field and store them.
        SOUND_BYTE_TYPES.forEach { type ->
            val entryName = type.simpleName!!
            val fieldName = entryName.toSentenceCase()
            val field = SoundByteToggles::class.declaredMemberProperties.firstOrNull { it.name.equals(fieldName, ignoreCase = true) }
            val value = field?.get(config)
            if (value is List<*>) {
                save(type, value.map {
                    try {
                        SoundFile.valueOf(it.toString())
                    } catch (e: IllegalArgumentException) {
                        println("Unknown sound file '$it' for '${type.simpleName?.toSentenceCase()}', ignoring...")
                        null
                    }
                })
            }
        }
    }

    /** Get all [SoundFile]s for a [SoundByte] type. May be empty. */
    fun get(key: KClass<out SoundByte>): List<SoundFile> {
        return map[key] ?: emptyList()
    }

    private fun save(key: KClass<out SoundByte>, choices: List<SoundFile?>) {
        val filtered = choices.filterNotNull()
        map[key] = filtered
    }

    private fun CharSequence.toSentenceCase(): String {
        return first().toLowerCase() + substring(1)
    }
}