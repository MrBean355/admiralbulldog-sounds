/*
 * Copyright 2024 Michael Johnston
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.mrbean355.admiralbulldog.persistence

import com.github.mrbean355.admiralbulldog.arch.DotaMod
import com.github.mrbean355.admiralbulldog.assets.ComboSoundBite
import com.github.mrbean355.admiralbulldog.assets.SoundBite
import com.github.mrbean355.admiralbulldog.assets.SoundBites
import com.github.mrbean355.admiralbulldog.common.DEFAULT_BOUNTY_RUNE_TIMER
import com.github.mrbean355.admiralbulldog.common.DEFAULT_CHANCE
import com.github.mrbean355.admiralbulldog.common.DEFAULT_MAX_PERIOD
import com.github.mrbean355.admiralbulldog.common.DEFAULT_MIN_PERIOD
import com.github.mrbean355.admiralbulldog.common.DEFAULT_RATE
import com.github.mrbean355.admiralbulldog.common.DEFAULT_VOLUME
import com.github.mrbean355.admiralbulldog.common.DEFAULT_WISDOM_RUNE_TIMER
import com.github.mrbean355.admiralbulldog.common.MAX_VOLUME
import com.github.mrbean355.admiralbulldog.common.MIN_VOLUME
import com.github.mrbean355.admiralbulldog.persistence.migration.ConfigMigration
import com.github.mrbean355.admiralbulldog.settings.UpdateFrequency
import com.github.mrbean355.admiralbulldog.triggers.SOUND_TRIGGER_TYPES
import com.github.mrbean355.admiralbulldog.triggers.SoundTriggerType
import com.google.gson.GsonBuilder
import org.slf4j.LoggerFactory
import java.io.File
import java.util.concurrent.TimeUnit

/** Version of the config file that this app supports. */
const val CONFIG_VERSION = 4

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

    fun getNextFeedback(): Long = loadedConfig.nextFeedback

    fun setNextFeedback(timeMillis: Long) {
        loadedConfig.nextFeedback = timeMillis
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

    fun setSoundsUpdateFrequency(frequency: UpdateFrequency) {
        loadedConfig.updates.soundsUpdateFrequency = frequency
        save()
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

    fun getBountyRuneTimer(): Int {
        return loadedConfig.special.bountyRuneTimer
    }

    fun setBountyRuneTimer(timer: Int) {
        loadedConfig.special.bountyRuneTimer = timer
        save()
    }

    fun getWisdomRuneTimer(): Int {
        return loadedConfig.special.wisdomRuneTimer
    }

    fun setWisdomRuneTimer(timer: Int) {
        loadedConfig.special.wisdomRuneTimer = timer
        save()
    }

    /** @return the current volume, in the range `[0.0, 100.0]`. */
    fun getVolume(): Int = loadedConfig.volume

    /** Set the current volume. Will be clamped to the range `[0.0, 100.0]`. */
    fun setVolume(volume: Int) {
        loadedConfig.volume = volume.coerceAtLeast(MIN_VOLUME).coerceAtMost(MAX_VOLUME)
        save()
    }

    fun isDarkMode(): Boolean = loadedConfig.darkMode

    fun setIsDarkMode(darkMode: Boolean) {
        loadedConfig.darkMode = darkMode
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

    /** @return sound trigger types that are enabled. */
    fun getEnabledSoundTriggers(): Collection<SoundTriggerType> {
        return loadedConfig.sounds
            .filterValues { it.enabled }
            .keys
            .map { SOUND_TRIGGER_TYPES.first { type -> it == type.simpleName } }
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

    fun getSoundTriggerChance(type: SoundTriggerType): Int {
        return loadedConfig.sounds.getValue(type.key).chance
    }

    fun setSoundTriggerChance(type: SoundTriggerType, chance: Int) {
        loadedConfig.sounds.getValue(type.key).chance = chance
        save()
    }

    fun getSoundTriggerMinRate(type: SoundTriggerType): Int {
        return loadedConfig.sounds.getValue(type.key).minRate
    }

    fun setSoundTriggerMinRate(type: SoundTriggerType, minRate: Int) {
        loadedConfig.sounds.getValue(type.key).minRate = minRate
        save()
    }

    fun getSoundTriggerMaxRate(type: SoundTriggerType): Int {
        return loadedConfig.sounds.getValue(type.key).maxRate
    }

    fun setSoundTriggerMaxRate(type: SoundTriggerType, maxRate: Int) {
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

    fun getSoundBoardRate(): Int {
        return loadedConfig.soundBoardRate
    }

    fun setSoundBoardRate(rate: Int) {
        loadedConfig.soundBoardRate = rate
        save()
    }

    fun isModRiskAccepted(): Boolean = loadedConfig.modRiskAccepted

    fun setModRiskAccepted() {
        loadedConfig.modRiskAccepted = true
        save()
    }

    /** @return `true` if ay least one mod has been enabled. */
    fun hasEnabledMods(): Boolean {
        return loadedConfig.enabledMods.isNotEmpty()
    }

    fun getEnabledMods(): Collection<String> {
        return loadedConfig.enabledMods
    }

    /** @return `true` if the given mod has been enabled. */
    fun isModEnabled(mod: DotaMod): Boolean {
        return mod.key in loadedConfig.enabledMods
    }

    /** Enable the given mod. */
    fun enableMod(mod: DotaMod) {
        loadedConfig.enabledMods += mod.key
        save()
    }

    /** Set the list of all enabled mods. */
    fun setEnabledMods(mods: Collection<DotaMod>) {
        loadedConfig.enabledMods.setAll(mods.map { it.key })
        save()
    }

    /**
     * Returns a collection of sound names that were selected by the user but don't exist locally.
     * When called, the returned sounds will not be returned by future calls.
     */
    fun findInvalidSounds(): Collection<String> {
        val localSounds = SoundBites.getAll().map { it.name }
        val invalidSounds = ((loadedConfig.sounds.flatMap { it.value.sounds }) + loadedConfig.soundBoard)
            .distinct()
            .filter { it !in localSounds }
            .filter { it !in loadedConfig.invalidSounds }

        loadedConfig.invalidSounds += invalidSounds
        save()

        return invalidSounds
    }

    fun removeInvalidSounds(victims: Collection<String>) {
        loadedConfig.invalidSounds.removeAll(victims)
        save()
    }

    fun getSoundBiteVolumes(): Map<String, Int> {
        return loadedConfig.volumes.toMap()
    }

    fun getSoundBiteVolume(name: String): Int? {
        return loadedConfig.volumes[name]
    }

    fun addSoundBiteVolume(name: String, volume: Int) {
        loadedConfig.volumes[name] = volume
        save()
    }

    fun removeSoundBiteVolume(name: String) {
        loadedConfig.volumes.remove(name)
        save()
    }

    /** Update the given sound trigger's config to use the given sound bite `selection`. */
    fun saveSoundsForType(type: SoundTriggerType, selection: List<SoundBite>) {
        loadedConfig.sounds.getValue(type.key).sounds.setAll(selection.map { it.name })
        save()
    }

    fun getSoundCombos(): Collection<String> {
        return loadedConfig.soundCombos.keys
    }

    fun hasSoundCombo(name: String): Boolean {
        return loadedConfig.soundCombos.containsKey(name)
    }

    fun findSoundCombo(name: String): List<SoundBite> {
        return loadedConfig.soundCombos.getValue(name).mapNotNull {
            SoundBites.findSound(it)
        }
    }

    fun saveSoundCombo(name: String, sounds: List<SoundBite>) {
        loadedConfig.soundCombos[name] = sounds.map { it.name }
        save()
    }

    fun removeSoundCombo(soundBite: ComboSoundBite) {
        loadedConfig.soundCombos.remove(soundBite.name)
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
        return Config(
            nextFeedback = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(30),
            sounds = SOUND_TRIGGER_TYPES
                .map { it.key }
                .associateWith { Toggle() }
                .toMutableMap()
        )
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
        var nextFeedback: Long = 0,
        var updates: Updates = Updates(),
        var special: SpecialConfig = SpecialConfig(),
        var lastSync: Long = 0,
        var volume: Int = DEFAULT_VOLUME,
        var darkMode: Boolean = true,
        var discordBotEnabled: Boolean = false,
        var discordToken: String = "",
        var minimizeToTray: Boolean = true,
        var alwaysShowTrayIcon: Boolean = false,
        var trayNotified: Boolean = false,
        val sounds: MutableMap<String, Toggle> = mutableMapOf(),
        val soundBoard: MutableList<String> = mutableListOf(),
        var soundBoardRate: Int = DEFAULT_RATE,
        val invalidSounds: MutableSet<String> = mutableSetOf(),
        val volumes: MutableMap<String, Int> = mutableMapOf(),
        var modRiskAccepted: Boolean = false,
        val enabledMods: MutableSet<String> = mutableSetOf(),
        val soundCombos: MutableMap<String, List<String>> = mutableMapOf()
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
        var maxPeriod: Int = DEFAULT_MAX_PERIOD,
        var bountyRuneTimer: Int = DEFAULT_BOUNTY_RUNE_TIMER,
        var wisdomRuneTimer: Int = DEFAULT_WISDOM_RUNE_TIMER,
    )

    private data class Toggle(
        var enabled: Boolean = false,
        var chance: Int = DEFAULT_CHANCE,
        var minRate: Int = DEFAULT_RATE,
        var maxRate: Int = DEFAULT_RATE,
        var playThroughDiscord: Boolean = false,
        val sounds: MutableList<String> = mutableListOf()
    )
}