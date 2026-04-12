package com.github.mrbean355.admiralbulldog.sounds

import com.github.mrbean355.admiralbulldog.arch.ComposeViewModel
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import com.github.mrbean355.admiralbulldog.triggers.OnBountyRunesSpawn
import com.github.mrbean355.admiralbulldog.triggers.OnHeal
import com.github.mrbean355.admiralbulldog.triggers.OnWisdomRunesSpawn
import com.github.mrbean355.admiralbulldog.triggers.Periodically
import com.github.mrbean355.admiralbulldog.triggers.SoundTriggerType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ConfigureSoundTriggerViewModel(val type: SoundTriggerType) : ComposeViewModel() {

    /* Basic */
    val title: String = type.friendlyName
    val description: String = type.description

    private val _enabled = MutableStateFlow(ConfigPersistence.isSoundTriggerEnabled(type))
    val enabled: StateFlow<Boolean> = _enabled.asStateFlow()

    private val _bountyRuneTimer = MutableStateFlow(ConfigPersistence.getBountyRuneTimer())
    val bountyRuneTimer: StateFlow<Int> = _bountyRuneTimer.asStateFlow()

    val showBountyRuneTimer: Boolean = type == OnBountyRunesSpawn::class

    private val _wisdomRuneTimer = MutableStateFlow(ConfigPersistence.getWisdomRuneTimer())
    val wisdomRuneTimer: StateFlow<Int> = _wisdomRuneTimer.asStateFlow()

    val showWisdomRuneTimer: Boolean = type == OnWisdomRunesSpawn::class

    private val _soundBiteCount = MutableStateFlow(ConfigPersistence.getSoundsForType(type).size)
    val soundBiteCount: StateFlow<Int> = _soundBiteCount.asStateFlow()

    /* Chance to play */
    val showChance: Boolean = type != Periodically::class
    val showSmartChance: Boolean = type == OnHeal::class

    private val _useSmartChance = MutableStateFlow(ConfigPersistence.isUsingHealSmartChance())
    val useSmartChance: StateFlow<Boolean> = _useSmartChance.asStateFlow()

    val enableChanceSpinner: Boolean get() = !showSmartChance || !_useSmartChance.value

    private val _chance = MutableStateFlow(ConfigPersistence.getSoundTriggerChance(type))
    val chance: StateFlow<Int> = _chance.asStateFlow()

    /* Periodic */
    val showPeriod: Boolean = !showChance

    private val _minPeriod = MutableStateFlow(ConfigPersistence.getMinPeriod())
    val minPeriod: StateFlow<Int> = _minPeriod.asStateFlow()

    private val _maxPeriod = MutableStateFlow(ConfigPersistence.getMaxPeriod())
    val maxPeriod: StateFlow<Int> = _maxPeriod.asStateFlow()

    /* Playback rate */
    private val _minRate = MutableStateFlow(ConfigPersistence.getSoundTriggerMinRate(type))
    val minRate: StateFlow<Int> = _minRate.asStateFlow()

    private val _maxRate = MutableStateFlow(ConfigPersistence.getSoundTriggerMaxRate(type))
    val maxRate: StateFlow<Int> = _maxRate.asStateFlow()

    fun setEnabled(value: Boolean) {
        _enabled.value = value
        ConfigPersistence.toggleSoundTrigger(type, value)
    }

    fun setBountyRuneTimer(value: Int) {
        _bountyRuneTimer.value = value
        ConfigPersistence.setBountyRuneTimer(value)
    }

    fun setWisdomRuneTimer(value: Int) {
        _wisdomRuneTimer.value = value
        ConfigPersistence.setWisdomRuneTimer(value)
    }

    fun setUseSmartChance(value: Boolean) {
        _useSmartChance.value = value
        ConfigPersistence.setIsUsingHealSmartChance(value)
    }

    fun setChance(value: Int) {
        _chance.value = value
        ConfigPersistence.setSoundTriggerChance(type, value)
    }

    fun setMinPeriod(value: Int) {
        _minPeriod.value = value
        ConfigPersistence.setMinPeriod(value)
        if (value > _maxPeriod.value) {
            setMaxPeriod(value)
        }
    }

    fun setMaxPeriod(value: Int) {
        _maxPeriod.value = value
        ConfigPersistence.setMaxPeriod(value)
        if (value < _minPeriod.value) {
            setMinPeriod(value)
        }
    }

    fun setMinRate(value: Int) {
        _minRate.value = value
        ConfigPersistence.setSoundTriggerMinRate(type, value)
        if (value > _maxRate.value) {
            setMaxRate(value)
        }
    }

    fun setMaxRate(value: Int) {
        _maxRate.value = value
        ConfigPersistence.setSoundTriggerMaxRate(type, value)
        if (value < _minRate.value) {
            setMinRate(value)
        }
    }

    fun onChooseSoundsClicked() {
        // TODO: Implement ChooseSoundFilesScreen in Compose or keep as bridge
        // For now, let's assume we'll migrate it soon.
    }

    fun onTestPlaybackSpeedClicked() {
        // TODO: Implement TestPlaybackSpeedScreen in Compose
    }

    fun refreshSoundBiteCount() {
        _soundBiteCount.value = ConfigPersistence.getSoundsForType(type).size
    }
}