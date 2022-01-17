/*
 * Copyright 2022 Michael Johnston
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.mrbean355.admiralbulldog.sounds

import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import com.github.mrbean355.admiralbulldog.triggers.OnBountyRunesSpawn
import com.github.mrbean355.admiralbulldog.triggers.OnHeal
import com.github.mrbean355.admiralbulldog.triggers.Periodically
import com.github.mrbean355.admiralbulldog.triggers.SoundTriggerType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class ConfigureSoundTriggerViewModel(
    private val triggerType: SoundTriggerType
) {
    val enabled = MutableStateFlow(ConfigPersistence.isSoundTriggerEnabled(triggerType))
    val chance = MutableStateFlow(ConfigPersistence.getSoundTriggerChance(triggerType))
    val smartChanceEnabled = MutableStateFlow(ConfigPersistence.isUsingHealSmartChance())
    val showChanceSlider = smartChanceEnabled.map { !it or !showSmartChance }
    val runeTimer = MutableStateFlow(ConfigPersistence.getBountyRuneTimer())
    val period = MutableStateFlow(ConfigPersistence.getMinPeriod()..ConfigPersistence.getMaxPeriod())
    val rate = MutableStateFlow(ConfigPersistence.getSoundTriggerMinRate(triggerType)..ConfigPersistence.getSoundTriggerMaxRate(triggerType))
    val soundBites = MutableStateFlow(ConfigPersistence.getSoundsForType(triggerType))

    val showChance: Boolean get() = triggerType != Periodically::class
    val showSmartChance: Boolean get() = triggerType == OnHeal::class
    val showRuneTimer: Boolean get() = triggerType == OnBountyRunesSpawn::class
    val showPeriod: Boolean get() = triggerType == Periodically::class

    fun onEnabledChanged(newValue: Boolean) {
        ConfigPersistence.toggleSoundTrigger(triggerType, newValue)
        enabled.value = newValue
    }

    fun onChanceChanged(newValue: Int) {
        ConfigPersistence.setSoundTriggerChance(triggerType, newValue)
        chance.value = newValue
    }

    fun onSmartChanceChanged(newValue: Boolean) {
        ConfigPersistence.setIsUsingHealSmartChance(newValue)
        smartChanceEnabled.value = newValue
    }

    fun onRuneTimerChanged(newValue: Int) {
        ConfigPersistence.setBountyRuneTimer(newValue)
        runeTimer.value = newValue
    }

    fun onPeriodChanged(newValues: IntRange) {
        ConfigPersistence.setMinPeriod(newValues.first)
        ConfigPersistence.setMaxPeriod(newValues.last)
        period.value = newValues
    }

    fun onRateChanged(newValues: IntRange) {
        ConfigPersistence.setSoundTriggerMinRate(triggerType, newValues.first)
        ConfigPersistence.setSoundTriggerMaxRate(triggerType, newValues.last)
        rate.value = newValues
    }
}