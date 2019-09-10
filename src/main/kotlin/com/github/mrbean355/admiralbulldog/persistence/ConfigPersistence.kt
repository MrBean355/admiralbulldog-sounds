package com.github.mrbean355.admiralbulldog.persistence

import com.github.mrbean355.admiralbulldog.assets.SoundFile
import com.github.mrbean355.admiralbulldog.bytes.SOUND_BYTE_TYPES
import com.github.mrbean355.admiralbulldog.bytes.SoundByte
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.io.File
import java.lang.reflect.Type
import kotlin.reflect.KClass

private const val FILE_NAME = "config.json"
private const val DEFAULTS_PATH = "defaults/%s.json"
const val MIN_VOLUME = 0.0
const val MAX_VOLUME = 100.0
private const val DEFAULT_VOLUME = 20.0

/** List of all [SoundFile] enum entry names. */
private val SOUND_FILE_ENTRIES = SoundFile.values().map { it.name }

/**
 * Facilitates saving & loading the configuration of the sound bytes from a file.
 * Sound bytes:
 * - have a list of possible sounds to play, configurable by the user at runtime
 * - can be enabled or disabled at runtime by the user
 */
object ConfigPersistence : JsonDeserializer<SoundFile> {
    private val gson = GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(SoundFile::class.java, this)
            .create()
    private var volume = MIN_VOLUME
    private var discordBotEnabled: Boolean = false
    private var discordToken: String? = null
    private var config: MutableMap<String, Toggle> = mutableMapOf()
    private val invalidSounds = mutableSetOf<String>()

    /** Load config from file into memory. */
    fun initialise() {
        val file = File(FILE_NAME)
        val loadedConfig = if (file.exists()) {
            gson.fromJson(file.readText(), Config::class.java)
        } else {
            loadDefaultConfig()
        }
        volume = loadedConfig.volume
        discordBotEnabled = loadedConfig.discordBotEnabled
        discordToken = loadedConfig.discordToken
        config = loadedConfig.sounds.mapValues {
            Toggle(it.value.enabled, it.value.sounds.filterNotNull())
        }.toMutableMap()

        addMissingSoundByteDefaults()
        save()
    }

    /** @return the current volume, in the range `[0.0, 100.0]`. */
    fun getVolume() = volume

    /** Set the current volume. Will be clamped to the range `[0.0, 100.0]`. */
    fun setVolume(volume: Double) {
        this.volume = volume.coerceAtLeast(MIN_VOLUME).coerceAtMost(MAX_VOLUME)
        save()
    }

    /** @return `true` if the user has enabled the Discord bot. */
    fun isUsingDiscordBot() = discordBotEnabled

    /** @return the user's current token if it is set, empty string otherwise. */
    fun getDiscordToken() = discordToken.orEmpty()

    /** Store whether the Discord bot is enabled and the user's current token. */
    fun setDiscordToken(enabled: Boolean, discordToken: String?) {
        this.discordBotEnabled = enabled && !discordToken.isNullOrBlank()
        this.discordToken = discordToken?.trim()
        save()
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
    fun getSoundsForType(type: KClass<out SoundByte>): List<SoundFile> {
        val toggle = config[type.simpleName]!!
        if (toggle.enabled) {
            return toggle.sounds.filterNotNull()
        }
        return emptyList()
    }

    /** Update the given sound byte's config to use the given sound `selection`. */
    fun saveSoundsForType(type: KClass<out SoundByte>, selection: List<SoundFile>) {
        config[type.simpleName]!!.sounds = selection
        save()
    }

    fun getInvalidSounds() = invalidSounds.toList()

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): SoundFile? {
        val element = json.asString
        if (SOUND_FILE_ENTRIES.contains(element)) {
            return SoundFile.valueOf(element)
        }
        // The deserialised sound name doesn't match an entry in the SoundFile enum. This means the user downloaded a new
        // version of the app which doesn't have the sound file any more. Keep track of this so we can inform the user.
        invalidSounds += element
        return null
    }

    /** Save the current `config` map to file. */
    private fun save() {
        val file = File(FILE_NAME)
        if (!file.exists()) {
            file.createNewFile()
        }
        file.writeText(gson.toJson(Config(volume, discordBotEnabled, discordToken, config)))
    }

    /** Load the default configs for all sound bytes. */
    private fun loadDefaultConfig(): Config {
        val sounds = SOUND_BYTE_TYPES.associateWith { loadDefaults(it) }
                .mapKeys { it.key.simpleName!! }
        return Config(DEFAULT_VOLUME, false, null, sounds)
    }

    /** Load the default config for a given sound byte `type`. */
    private fun loadDefaults(type: KClass<out SoundByte>): Toggle {
        val resource = javaClass.classLoader.getResource(DEFAULTS_PATH.format(type.simpleName))
                ?: return Toggle(false, emptyList())

        return gson.fromJson(resource.readText(), Toggle::class.java)
    }

    /** Checks the loaded `config` map, adding defaults for any missing sound bytes. */
    private fun addMissingSoundByteDefaults() {
        SOUND_BYTE_TYPES.forEach {
            if (config[it.simpleName] == null) {
                config[it.simpleName!!] = loadDefaults(it)
            }
        }
    }

    data class Config(val volume: Double, val discordBotEnabled: Boolean, val discordToken: String?, val sounds: Map<String, Toggle>)

    data class Toggle(var enabled: Boolean, var sounds: List<SoundFile?>)
}