package com.github.mrbean355.admiralbulldog.persistence

import com.github.mrbean355.admiralbulldog.assets.SoundFile
import com.github.mrbean355.admiralbulldog.assets.SoundFiles
import com.github.mrbean355.admiralbulldog.bytes.SOUND_BYTE_TYPES
import com.github.mrbean355.admiralbulldog.bytes.SoundByte
import com.google.gson.GsonBuilder
import java.io.File
import kotlin.reflect.KClass

private const val FILE_NAME = "config.json"
private const val DEFAULTS_PATH = "defaults/%s.json"
private const val DEFAULT_PORT = 12345
const val MIN_VOLUME = 0.0
const val MAX_VOLUME = 100.0
private const val DEFAULT_VOLUME = 20.0

/**
 * Facilitates saving & loading the configuration of the sound bytes from a file.
 * Sound bytes:
 * - have a list of possible sounds to play, configurable by the user at runtime
 * - can be enabled or disabled at runtime by the user
 */
object ConfigPersistence {
    private lateinit var loadedConfig: Config
    private val gson = GsonBuilder()
            .setPrettyPrinting()
            .create()

    /** Load config from file into memory. */
    fun initialise() {
        val file = File(FILE_NAME)
        loadedConfig = if (file.exists()) {
            gson.fromJson(file.readText(), Config::class.java)
        } else {
            loadDefaultConfig()
        }
        if (loadedConfig.port == 0) {
            loadedConfig.port = DEFAULT_PORT
        }
        addMissingSoundByteDefaults()
        save()
    }

    /** @return the chosen port number. */
    fun getPort() = loadedConfig.port

    /** @return the user's ID. */
    fun getId() = loadedConfig.id

    /** Set the user's ID. */
    fun setId(id: String) {
        if (loadedConfig.id != id) {
            loadedConfig.id = id
            save()
        }
    }

    /** @return the time when sounds were last synced from the PlaySounds page. */
    fun getLastSync() = loadedConfig.lastSync

    /** Set the time when sounds were last synced from the PlaySounds page to now. */
    fun markLastSync() {
        loadedConfig.lastSync = System.currentTimeMillis()
        save()
    }

    /** @return the current volume, in the range `[0.0, 100.0]`. */
    fun getVolume() = loadedConfig.volume

    /** Set the current volume. Will be clamped to the range `[0.0, 100.0]`. */
    fun setVolume(volume: Double) {
        loadedConfig.volume = volume.coerceAtLeast(MIN_VOLUME).coerceAtMost(MAX_VOLUME)
        save()
    }

    /** @return `true` if the user has enabled the Discord bot. */
    fun isUsingDiscordBot() = loadedConfig.discordBotEnabled

    /** @return the user's current token if it is set, empty string otherwise. */
    fun getDiscordToken() = loadedConfig.discordToken.orEmpty()

    /** Store whether the Discord bot is enabled and the user's current token. */
    fun setDiscordToken(enabled: Boolean, discordToken: String?) {
        loadedConfig.discordBotEnabled = enabled && !discordToken.isNullOrBlank()
        loadedConfig.discordToken = discordToken?.trim()
        save()
    }

    /** @return `true` if the sound byte is enabled; `false` otherwise. */
    fun isSoundByteEnabled(type: KClass<out SoundByte>): Boolean {
        return loadedConfig.sounds[type.simpleName]!!.enabled
    }

    /** Enable or disable a sound byte. */
    fun toggleSoundByte(type: KClass<out SoundByte>, enabled: Boolean) {
        loadedConfig.sounds[type.simpleName]!!.enabled = enabled
        save()
    }

    /** @return whether the user has chosen to play the sound byte through Discord. */
    fun isPlayedThroughDiscord(type: KClass<out SoundByte>): Boolean {
        return loadedConfig.sounds[type.simpleName]!!.playThroughDiscord
    }

    /** Set whether the user has chosen to play the sound byte through Discord. */
    fun setPlayedThroughDiscord(type: KClass<out SoundByte>, playThroughDiscord: Boolean) {
        loadedConfig.sounds[type.simpleName]!!.playThroughDiscord = playThroughDiscord
        save()
    }

    /** @return all selected sounds for a sound byte if it's enabled; empty list otherwise. */
    fun getSoundsForType(type: KClass<out SoundByte>): List<SoundFile> {
        val toggle = loadedConfig.sounds[type.simpleName]!!
        if (toggle.enabled) {
            return toggle.sounds.mapNotNull { SoundFiles.findSound(it) }
        }
        return emptyList()
    }

    /** @return a list of user-selected sounds that don't exist on the PlaySounds page. */
    fun getInvalidSounds(): List<String> {
        val existing = SoundFiles.getAll().map { it.name }
        return loadedConfig.sounds.flatMap { it.value.sounds }
                .filter { it !in existing }
    }

    /** Clear user-selected sounds that don't exist on the PlaySounds page from the config. */
    fun clearInvalidSounds() {
        val invalid = getInvalidSounds()
        loadedConfig.sounds.forEach { (_, v) ->
            v.sounds.removeAll { it in invalid }
        }
        save()
    }

    /** Update the given sound byte's config to use the given sound `selection`. */
    fun saveSoundsForType(type: KClass<out SoundByte>, selection: List<SoundFile>) {
        loadedConfig.sounds[type.simpleName]!!.sounds = selection.map { it.name }.toMutableList()
        save()
    }

    /** Save the current `config` map to file. */
    private fun save() {
        val file = File(FILE_NAME)
        if (!file.exists()) {
            file.createNewFile()
        }
        file.writeText(gson.toJson(loadedConfig))
    }

    /** Load the default configs for all sound bytes. */
    private fun loadDefaultConfig(): Config {
        val sounds = SOUND_BYTE_TYPES.associateWith { loadDefaults(it) }
                .mapKeys { it.key.simpleName!! }
        return Config(0, null, 0L, DEFAULT_VOLUME, false, null, sounds.toMutableMap())
    }

    /** Load the default config for a given sound byte `type`. */
    private fun loadDefaults(type: KClass<out SoundByte>): Toggle {
        val resource = javaClass.classLoader.getResource(DEFAULTS_PATH.format(type.simpleName))
                ?: return Toggle(enabled = false, playThroughDiscord = false, sounds = mutableListOf())

        return gson.fromJson(resource.readText(), Toggle::class.java)
    }

    /** Checks the loaded `config` map, adding defaults for any missing sound bytes. */
    private fun addMissingSoundByteDefaults() {
        SOUND_BYTE_TYPES.forEach {
            if (loadedConfig.sounds[it.simpleName] == null) {
                loadedConfig.sounds[it.simpleName!!] = loadDefaults(it)
            }
        }
    }

    private data class Config(var port: Int, var id: String?, var lastSync: Long, var volume: Double, var discordBotEnabled: Boolean, var discordToken: String?, val sounds: MutableMap<String, Toggle>)

    private data class Toggle(var enabled: Boolean, var playThroughDiscord: Boolean, var sounds: MutableList<String>)
}