package com.github.mrbean355.admiralbulldog.persistence

import com.github.mrbean355.admiralbulldog.assets.SoundBite
import com.github.mrbean355.admiralbulldog.assets.SoundBites
import com.github.mrbean355.admiralbulldog.common.DEFAULT_CHANCE
import com.github.mrbean355.admiralbulldog.common.DEFAULT_MAX_PERIOD
import com.github.mrbean355.admiralbulldog.common.DEFAULT_MIN_PERIOD
import com.github.mrbean355.admiralbulldog.common.DEFAULT_RATE
import com.github.mrbean355.admiralbulldog.common.DEFAULT_VOLUME
import com.github.mrbean355.admiralbulldog.common.MAX_VOLUME
import com.github.mrbean355.admiralbulldog.common.MIN_VOLUME
import com.github.mrbean355.admiralbulldog.persistence.migration.ConfigMigration
import com.github.mrbean355.admiralbulldog.settings.UpdateFrequency
import com.github.mrbean355.admiralbulldog.triggers.SOUND_TRIGGER_TYPES
import com.github.mrbean355.admiralbulldog.triggers.SoundTriggerType
import com.google.gson.GsonBuilder
import org.slf4j.LoggerFactory
import java.io.File

/** Version of the config file that this app supports. */
const val CONFIG_VERSION = 1

private const val FILE_NAME = "config.json"
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
            val jsonElement = ConfigMigration().run(file.readText())
            gson.fromJson(jsonElement, Config::class.java)
        } else {
            loadDefaultConfig()
        }
        if (loadedConfig.port == 0) {
            loadedConfig.port = DEFAULT_PORT
            logger.info("Defaulting to port $DEFAULT_PORT")
        }
        cleanUpStaleSoundTriggers()
        save()
    }

    /** @return the chosen port number. */
    fun getPort() = loadedConfig.port

    /** @return the user's ID. */
    fun getId(): String = loadedConfig.id

    /** Set the user's ID. */
    fun setId(id: String) {
        if (loadedConfig.id != id) {
            loadedConfig.id = id
            save()
        }
    }

    /** @return the currently chosen Dota installation path. */
    fun getDotaPath(): String {
        return loadedConfig.dotaPath
    }

    /** Set the Dota installation path. */
    fun setDotaPath(path: String) {
        loadedConfig.dotaPath = path
        save()
    }

    fun getAppUpdateFrequency(): UpdateFrequency {
        return loadedConfig.updates.appUpdateFrequency
    }

    fun setAppUpdateFrequency(frequency: UpdateFrequency) {
        loadedConfig.updates.appUpdateFrequency = frequency
        save()
    }

    fun getAppLastUpdateAt(): Long {
        return loadedConfig.updates.appUpdateCheck
    }

    fun setAppLastUpdateToNow() {
        loadedConfig.updates.appUpdateCheck = System.currentTimeMillis()
        save()
    }

    fun getSoundsUpdateFrequency(): UpdateFrequency {
        return loadedConfig.updates.soundsUpdateFrequency
    }

    fun getSoundsLastUpdateAt(): Long {
        return loadedConfig.lastSync
    }

    fun setSoundsLastUpdateToNow() {
        loadedConfig.lastSync = System.currentTimeMillis()
        save()
    }

    fun getModUpdateFrequency(): UpdateFrequency {
        return loadedConfig.updates.modUpdateFrequency
    }

    fun setModUpdateFrequency(frequency: UpdateFrequency) {
        loadedConfig.updates.modUpdateFrequency = frequency
        save()
    }

    fun getModLastUpdateAt(): Long {
        return loadedConfig.updates.modUpdateCheck
    }

    fun setModLastUpdateToNow() {
        loadedConfig.updates.modUpdateCheck = System.currentTimeMillis()
        save()
    }

    fun isUsingHealSmartChance(): Boolean {
        return loadedConfig.special.useHealSmartChance
    }

    fun setIsUsingHealSmartChance(using: Boolean) {
        loadedConfig.special.useHealSmartChance = using
        save()
    }

    fun getMinPeriod(): Int {
        return loadedConfig.special.minPeriod
    }

    fun setMinPeriod(value: Int) {
        loadedConfig.special.minPeriod = value
        save()
    }

    fun getMaxPeriod(): Int {
        return loadedConfig.special.maxPeriod
    }

    fun setMaxPeriod(value: Int) {
        loadedConfig.special.maxPeriod = value
        save()
    }

    /** @return the current volume, in the range `[0.0, 100.0]`. */
    fun getVolume(): Int = loadedConfig.volume

    /** Set the current volume. Will be clamped to the range `[0.0, 100.0]`. */
    fun setVolume(volume: Int) {
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
    fun getDiscordToken(): String = loadedConfig.discordToken

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

    fun isMinimizeToTray(): Boolean = loadedConfig.minimizeToTray

    fun setMinimizeToTray(minimize: Boolean) {
        loadedConfig.minimizeToTray = minimize
        save()
    }

    fun isAlwaysShowTrayIcon(): Boolean = loadedConfig.alwaysShowTrayIcon

    fun setAlwaysShowTrayIcon(show: Boolean) {
        loadedConfig.alwaysShowTrayIcon = show
        save()
    }

    /** @return `true` if the sound trigger is enabled; `false` otherwise. */
    fun isSoundTriggerEnabled(type: SoundTriggerType): Boolean {
        return loadedConfig.sounds.getValue(type.key).enabled
    }

    /** Enable or disable a sound trigger. */
    fun toggleSoundTrigger(type: SoundTriggerType, enabled: Boolean) {
        loadedConfig.sounds.getValue(type.key).enabled = enabled
        save()
    }

    fun getSoundTriggerChance(type: SoundTriggerType): Double {
        return loadedConfig.sounds.getValue(type.key).chance
    }

    fun setSoundTriggerChance(type: SoundTriggerType, chance: Double) {
        loadedConfig.sounds.getValue(type.key).chance = chance
        save()
    }

    fun getSoundTriggerMinRate(type: SoundTriggerType): Double {
        return loadedConfig.sounds.getValue(type.key).minRate
    }

    fun setSoundTriggerMinRate(type: SoundTriggerType, minRate: Double) {
        loadedConfig.sounds.getValue(type.key).minRate = minRate
        save()
    }

    fun getSoundTriggerMaxRate(type: SoundTriggerType): Double {
        return loadedConfig.sounds.getValue(type.key).maxRate
    }

    fun setSoundTriggerMaxRate(type: SoundTriggerType, maxRate: Double) {
        loadedConfig.sounds.getValue(type.key).maxRate = maxRate
        save()
    }

    /** @return whether the user has chosen to play the sound trigger through Discord. */
    fun isPlayedThroughDiscord(type: SoundTriggerType): Boolean {
        return loadedConfig.sounds.getValue(type.key).playThroughDiscord
    }

    /** Set whether the user has chosen to play the sound trigger through Discord. */
    fun setPlayedThroughDiscord(type: SoundTriggerType, playThroughDiscord: Boolean) {
        loadedConfig.sounds.getValue(type.key).playThroughDiscord = playThroughDiscord
        save()
    }

    /** @return all selected sound bites for a trigger. */
    fun getSoundsForType(type: SoundTriggerType): List<SoundBite> {
        val toggle = loadedConfig.sounds.getValue(type.key)
        return toggle.sounds.mapNotNull { SoundBites.findSound(it) }
    }

    fun isSoundSelectedForType(type: SoundTriggerType, soundBite: SoundBite): Boolean {
        return loadedConfig.sounds.getValue(type.key).sounds.contains(soundBite.name)
    }

    fun setSoundSelectedForType(type: SoundTriggerType, soundBite: SoundBite, selected: Boolean) {
        loadedConfig.sounds.getValue(type.key).sounds.let {
            if (selected) it += soundBite.name else it -= soundBite.name
        }
        save()
    }

    /** @return a list of all sounds selected for the sound board. */
    fun getSoundBoard(): List<SoundBite> {
        return loadedConfig.soundBoard.mapNotNull { SoundBites.findSound(it) }
    }

    /** Set the list of all sounds selected for the sound board. */
    fun setSoundBoard(soundBoard: List<SoundBite>) {
        loadedConfig.soundBoard.setAll(soundBoard.map { it.name })
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

    /** @return the version of the currently installed mod, or an empty string if there is none. */
    fun getModVersion(): String {
        return loadedConfig.modVersion
    }

    /** Set the version of the currently installed mod. */
    fun setModVersion(version: String) {
        loadedConfig.modVersion = version
        save()
    }

    /**
     * Returns a collection of sound names that were selected by the user but don't exist locally.
     * When called, the returned sounds will not be returned by future calls.
     */
    fun takeInvalidSounds(): Collection<String> {
        val localSounds = SoundBites.getAll().map { it.name }
        val invalidSounds = ((loadedConfig.sounds.flatMap { it.value.sounds }) + loadedConfig.soundBoard)
                .distinct()
                .filter { it !in localSounds }
                .filter { it !in loadedConfig.invalidSounds }

        loadedConfig.invalidSounds += invalidSounds
        save()

        return invalidSounds
    }

    /** Update the given sound trigger's config to use the given sound bite `selection`. */
    fun saveSoundsForType(type: SoundTriggerType, selection: List<SoundBite>) {
        loadedConfig.sounds.getValue(type.key).sounds.setAll(selection.map { it.name })
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

    /** Load the default configs for all sound triggers. */
    private fun loadDefaultConfig(): Config {
        return Config(sounds = SOUND_TRIGGER_TYPES
                .map { it.key }
                .associateWith { Toggle() }
                .toMutableMap()
        )
    }

    private fun cleanUpStaleSoundTriggers() {
        val validTypes = SOUND_TRIGGER_TYPES.map { it.key }
        val invalidTypes = loadedConfig.sounds.filterKeys { it !in validTypes }
        invalidTypes.forEach {
            loadedConfig.sounds.remove(it.key)
            logger.info("Removed stale sound trigger: ${it.key}")
        }
    }

    private val SoundTriggerType.key: String
        get() = simpleName ?: java.name

    private fun <T> MutableCollection<T>.setAll(newItems: Iterable<T>) {
        clear()
        addAll(newItems)
    }

    private data class Config(
            var version: Int = CONFIG_VERSION,
            var port: Int = DEFAULT_PORT,
            var id: String = "",
            var dotaPath: String = "",
            var updates: Updates = Updates(),
            var special: SpecialConfig = SpecialConfig(),
            var lastSync: Long = 0,
            var volume: Int = DEFAULT_VOLUME,
            var discordBotEnabled: Boolean = false,
            var discordToken: String = "",
            var minimizeToTray: Boolean = true,
            var alwaysShowTrayIcon: Boolean = false,
            var trayNotified: Boolean = false,
            val sounds: MutableMap<String, Toggle> = mutableMapOf(),
            val soundBoard: MutableList<String> = mutableListOf(),
            val invalidSounds: MutableSet<String> = mutableSetOf(),
            var modEnabled: Boolean = false,
            var modTempDisabled: Boolean = false,
            var modVersion: String = ""
    )

    private data class Updates(
            var appUpdateCheck: Long = 0,
            var appUpdateFrequency: UpdateFrequency = UpdateFrequency.WEEKLY,
            var soundsUpdateFrequency: UpdateFrequency = UpdateFrequency.DAILY,
            var modUpdateCheck: Long = 0,
            var modUpdateFrequency: UpdateFrequency = UpdateFrequency.ALWAYS
    )

    private data class SpecialConfig(
            var useHealSmartChance: Boolean = true,
            var minPeriod: Int = DEFAULT_MIN_PERIOD,
            var maxPeriod: Int = DEFAULT_MAX_PERIOD
    )

    private data class Toggle(
            var enabled: Boolean = false,
            var chance: Double = DEFAULT_CHANCE,
            var minRate: Double = DEFAULT_RATE,
            var maxRate: Double = DEFAULT_RATE,
            var playThroughDiscord: Boolean = false,
            val sounds: MutableList<String> = mutableListOf()
    )
}