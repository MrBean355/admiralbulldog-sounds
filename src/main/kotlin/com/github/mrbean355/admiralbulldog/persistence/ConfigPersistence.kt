package com.github.mrbean355.admiralbulldog.persistence

import com.github.mrbean355.admiralbulldog.assets.SoundBite
import com.github.mrbean355.admiralbulldog.assets.SoundBites
import com.github.mrbean355.admiralbulldog.common.DEFAULT_CHANCE
import com.github.mrbean355.admiralbulldog.common.DEFAULT_RATE
import com.github.mrbean355.admiralbulldog.common.DEFAULT_VOLUME
import com.github.mrbean355.admiralbulldog.common.MAX_VOLUME
import com.github.mrbean355.admiralbulldog.common.MIN_VOLUME
import com.github.mrbean355.admiralbulldog.events.SOUND_EVENT_TYPES
import com.github.mrbean355.admiralbulldog.events.SoundEvent
import com.github.mrbean355.admiralbulldog.settings.UpdateFrequency
import com.google.gson.GsonBuilder
import org.slf4j.LoggerFactory
import java.io.File
import kotlin.reflect.KClass

private const val FILE_NAME = "config.json"
private const val DEFAULTS_PATH = "defaults/%s.json"
private const val DEFAULT_PORT = 12345

/**
 * Facilitates saving & loading the configuration of the sound bites from a file.
 * Sound bites:
 * - have a list of possible sounds to play, configurable by the user at runtime
 * - can be enabled or disabled at runtime by the user
 */
object ConfigPersistence {
    private val logger = LoggerFactory.getLogger(ConfigPersistence::class.java)
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
            logger.info("Defaulting to port $DEFAULT_PORT")
        }
        cleanUpStaleSoundEvents()
        migrateFromOldConfig()
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

    /** @return the currently chosen Dota installation path. */
    fun getDotaPath(): String? {
        return loadedConfig.dotaPath
    }

    /** Set the Dota installation path. */
    fun setDotaPath(path: String) {
        loadedConfig.dotaPath = path
        save()
    }

    fun getAppUpdateFrequency(): UpdateFrequency {
        return loadedConfig.updates?.appUpdateFrequency!!
    }

    fun setAppUpdateFrequency(frequency: UpdateFrequency) {
        loadedConfig.updates?.appUpdateFrequency = frequency
        save()
    }

    fun getAppLastUpdateAt(): Long {
        return loadedConfig.updates?.appUpdateCheck ?: 0
    }

    fun setAppLastUpdateToNow() {
        loadedConfig.updates?.appUpdateCheck = System.currentTimeMillis()
        save()
    }

    fun getSoundsUpdateFrequency(): UpdateFrequency {
        return loadedConfig.updates?.soundsUpdateFrequency!!
    }

    fun getSoundsLastUpdateAt(): Long {
        return loadedConfig.lastSync
    }

    fun setSoundsLastUpdateToNow() {
        loadedConfig.lastSync = System.currentTimeMillis()
        save()
    }

    fun getModUpdateFrequency(): UpdateFrequency {
        return loadedConfig.updates?.modUpdateFrequency!!
    }

    fun setModUpdateFrequency(frequency: UpdateFrequency) {
        loadedConfig.updates?.modUpdateFrequency = frequency
        save()
    }

    fun getModLastUpdateAt(): Long {
        return loadedConfig.updates?.modUpdateCheck ?: 0
    }

    fun setModLastUpdateToNow() {
        loadedConfig.updates?.modUpdateCheck = System.currentTimeMillis()
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

    /** Set whether the user has enabled the Discord bot. */
    fun setUsingDiscordBot(enabled: Boolean) {
        loadedConfig.discordBotEnabled = enabled
        save()
    }

    /** @return the user's current token if it is set, empty string otherwise. */
    fun getDiscordToken() = loadedConfig.discordToken.orEmpty()

    /** Set the user's Discord token. */
    fun setDiscordToken(token: String) {
        loadedConfig.discordToken = token.trim()
        save()
    }

    /**
     * Sets whether we have notified about the tray icon to `true`.
     * @return the value prior to setting it to `true`.
     */
    fun getAndSetNotifiedAboutSystemTray(): Boolean {
        val previous = loadedConfig.trayNotified
        loadedConfig.trayNotified = true
        save()
        return previous
    }

    /** @return `true` if the sound bite is enabled; `false` otherwise. */
    fun isSoundEventEnabled(type: KClass<out SoundEvent>): Boolean {
        return loadedConfig.sounds[type.simpleName]!!.enabled
    }

    /** Enable or disable a sound bite. */
    fun toggleSoundEvent(type: KClass<out SoundEvent>, enabled: Boolean) {
        loadedConfig.sounds[type.simpleName]!!.enabled = enabled
        save()
    }

    fun getSoundEventChance(type: KClass<out SoundEvent>): Double {
        return loadedConfig.sounds[type.simpleName]?.chance ?: 0.0
    }

    fun setSoundEventChance(type: KClass<out SoundEvent>, chance: Double) {
        loadedConfig.sounds[type.simpleName]?.chance = chance
        save()
    }

    fun getSoundEventMinRate(type: KClass<out SoundEvent>): Double {
        return loadedConfig.sounds[type.simpleName]?.minRate ?: 0.0
    }

    fun setSoundEventMinRate(type: KClass<out SoundEvent>, minRate: Double) {
        loadedConfig.sounds[type.simpleName]?.minRate = minRate
        save()
    }

    fun getSoundEventMaxRate(type: KClass<out SoundEvent>): Double {
        return loadedConfig.sounds[type.simpleName]?.maxRate ?: 0.0
    }

    fun setSoundEventMaxRate(type: KClass<out SoundEvent>, maxRate: Double) {
        loadedConfig.sounds[type.simpleName]?.maxRate = maxRate
        save()
    }

    /** @return whether the user has chosen to play the sound bite through Discord. */
    fun isPlayedThroughDiscord(type: KClass<out SoundEvent>): Boolean {
        return loadedConfig.sounds[type.simpleName]!!.playThroughDiscord
    }

    /** Set whether the user has chosen to play the sound bite through Discord. */
    fun setPlayedThroughDiscord(type: KClass<out SoundEvent>, playThroughDiscord: Boolean) {
        loadedConfig.sounds[type.simpleName]!!.playThroughDiscord = playThroughDiscord
        save()
    }

    /** @return all selected sounds for a sound bite if it's enabled; empty list otherwise. */
    fun getSoundsForType(type: KClass<out SoundEvent>): List<SoundBite> {
        val toggle = loadedConfig.sounds[type.simpleName]!!
        if (toggle.enabled) {
            return toggle.sounds.mapNotNull { SoundBites.findSound(it) }
        }
        return emptyList()
    }

    /** @return a list of all sounds selected for the sound board. */
    fun getSoundBoard(): List<SoundBite> {
        return loadedConfig.soundBoard.orEmpty().mapNotNull { SoundBites.findSound(it) }
    }

    /** Set the list of all sounds selected for the sound board. */
    fun setSoundBoard(soundBoard: List<SoundBite>) {
        loadedConfig.soundBoard = soundBoard.map { it.name }
        save()
    }

    /** @return whether the user has enabled the mod. */
    fun isModEnabled(): Boolean {
        return loadedConfig.modEnabled
    }

    /** Set whether the user has enabled the mod. */
    fun setModEnabled(enabled: Boolean) {
        loadedConfig.modEnabled = enabled
        save()
    }

    fun isModTempDisabled(): Boolean {
        return loadedConfig.modTempDisabled
    }

    fun setModTempDisabled(disabled: Boolean) {
        loadedConfig.modTempDisabled = disabled
        save()
    }

    /** @return the version of the currently installed mod, or "0.0.0" if there is none. */
    fun getModVersion(): String {
        return loadedConfig.modVersion ?: "0.0.0"
    }

    /** Set the version of the currently installed mod. */
    fun setModVersion(version: String) {
        loadedConfig.modVersion = version
        save()
    }

    /** @return a list of user-selected sounds that don't exist on the PlaySounds page. */
    fun getInvalidSounds(): Collection<String> {
        val existing = SoundBites.getAll().map { it.name }
        val invalidSounds = loadedConfig.sounds
                .flatMap { it.value.sounds }
                .filter { it !in existing }
        val invalidSoundBoard = loadedConfig.soundBoard
                ?.filter { it !in existing }
                .orEmpty()
        return (invalidSounds + invalidSoundBoard).toSet()
    }

    /** Clear user-selected sounds that don't exist on the PlaySounds page from the config. */
    fun clearInvalidSounds() {
        val invalid = getInvalidSounds()
        loadedConfig.sounds.forEach { (_, v) ->
            v.sounds.removeAll { it in invalid }
        }
        loadedConfig.soundBoard = loadedConfig.soundBoard?.filterNot { it in invalid }
        save()
    }

    /** Update the given sound bite's config to use the given sound `selection`. */
    fun saveSoundsForType(type: KClass<out SoundEvent>, selection: List<SoundBite>) {
        loadedConfig.sounds[type.simpleName]!!.sounds = selection.map { it.name }.toMutableList()
        save()
    }

    /** Save the current `config` map to file. */
    private fun save() {
        val file = File(FILE_NAME)
        if (!file.exists()) {
            logger.info("Created new config file: ${file.absolutePath}")
            file.createNewFile()
        }
        file.writeText(gson.toJson(loadedConfig))
    }

    /** Load the default configs for all sound bites. */
    private fun loadDefaultConfig(): Config {
        val sounds = SOUND_EVENT_TYPES.associateWith { loadDefaults(it) }
                .mapKeys { it.key.simpleName!! }
        return Config(sounds = sounds.toMutableMap())
    }

    /** Load the default config for a given sound bite `type`. */
    private fun loadDefaults(type: KClass<out SoundEvent>): Toggle {
        val resource = javaClass.classLoader.getResource(DEFAULTS_PATH.format(type.simpleName))
        if (resource == null) {
            logger.warn("No defaults resource available for: ${type.simpleName}")
            return Toggle()
        }
        return gson.fromJson(resource.readText(), Toggle::class.java)
    }

    private fun cleanUpStaleSoundEvents() {
        val validTypes = SOUND_EVENT_TYPES.map { it.simpleName!! }
        val invalidTypes = loadedConfig.sounds.filterKeys { it !in validTypes }
        invalidTypes.forEach {
            loadedConfig.sounds.remove(it.key)
            logger.info("Removed stale sound event: ${it.key}")
        }
    }

    /** Checks the loaded `config` map, adding defaults for any missing sound bites. */
    private fun migrateFromOldConfig() {
        if (loadedConfig.updates == null) {
            loadedConfig.updates = Updates()
        }
        SOUND_EVENT_TYPES.forEach {
            if (loadedConfig.sounds[it.simpleName] == null) {
                loadedConfig.sounds[it.simpleName!!] = loadDefaults(it)
                logger.info("Loaded defaults for sound event: ${it.simpleName}")
            }
        }
        loadedConfig.sounds.forEach { (_, toggle) ->
            if (toggle.chance == null) toggle.chance = DEFAULT_CHANCE
            if (toggle.minRate == null) toggle.minRate = DEFAULT_RATE
            if (toggle.maxRate == null) toggle.maxRate = DEFAULT_RATE
        }
    }

    private data class Config(
            var port: Int = DEFAULT_PORT,
            var id: String? = null,
            var dotaPath: String? = null,
            var updates: Updates? = Updates(),
            var lastSync: Long = 0,
            var volume: Double = DEFAULT_VOLUME,
            var discordBotEnabled: Boolean = false,
            var discordToken: String? = null,
            var trayNotified: Boolean = false,
            val sounds: MutableMap<String, Toggle> = mutableMapOf(),
            var soundBoard: List<String>? = listOf(),
            var modEnabled: Boolean = false,
            var modTempDisabled: Boolean = false,
            var modVersion: String? = null
    )

    private data class Updates(
            var appUpdateCheck: Long = 0,
            var appUpdateFrequency: UpdateFrequency = UpdateFrequency.WEEKLY,
            var soundsUpdateFrequency: UpdateFrequency = UpdateFrequency.DAILY,
            var modUpdateCheck: Long? = 0,
            var modUpdateFrequency: UpdateFrequency = UpdateFrequency.ALWAYS
    )

    private data class Toggle(
            var enabled: Boolean = false,
            var chance: Double? = null,
            var minRate: Double? = null,
            var maxRate: Double? = null,
            var playThroughDiscord: Boolean = false,
            var sounds: MutableList<String> = mutableListOf()
    )
}