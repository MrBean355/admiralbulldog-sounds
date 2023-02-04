/*
 * Copyright 2023 Michael Johnston
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

package com.github.mrbean355.admiralbulldog.sounds

import com.github.mrbean355.admiralbulldog.arch.AppViewModel
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import com.github.mrbean355.admiralbulldog.triggers.OnBountyRunesSpawn
import com.github.mrbean355.admiralbulldog.triggers.OnHeal
import com.github.mrbean355.admiralbulldog.triggers.Periodically
import com.github.mrbean355.admiralbulldog.triggers.SoundTriggerType
import javafx.beans.binding.BooleanBinding
import javafx.beans.property.BooleanProperty
import javafx.beans.property.IntegerProperty
import javafx.beans.property.StringProperty
import tornadofx.booleanProperty
import tornadofx.intProperty
import tornadofx.onChange
import tornadofx.stringProperty

class ConfigureSoundTriggerViewModel : AppViewModel() {
    private val type: SoundTriggerType by param()

    /* Basic */
    val title: StringProperty = stringProperty(type.friendlyName)
    val description: StringProperty = stringProperty(type.description)
    val enabled: BooleanProperty = booleanProperty(ConfigPersistence.isSoundTriggerEnabled(type))
    val bountyRuneTimer: IntegerProperty = intProperty(ConfigPersistence.getBountyRuneTimer())
    val showBountyRuneTimer: BooleanProperty = booleanProperty(type == OnBountyRunesSpawn::class)
    val soundBiteCount = stringProperty(ConfigPersistence.getSoundsForType(type).size.toString())

    /* Chance to play */
    val showChance: BooleanProperty = booleanProperty(type != Periodically::class)
    val showSmartChance: BooleanProperty = booleanProperty(type == OnHeal::class)
    val useSmartChance: BooleanProperty = booleanProperty(ConfigPersistence.isUsingHealSmartChance())
    val enableChanceSpinner: BooleanBinding = showSmartChance.not().or(useSmartChance.not())
    val chance: IntegerProperty = intProperty(ConfigPersistence.getSoundTriggerChance(type))

    /* Periodic */
    val showPeriod: BooleanBinding = showChance.not()
    val minPeriod: IntegerProperty = intProperty(ConfigPersistence.getMinPeriod())
    val maxPeriod: IntegerProperty = intProperty(ConfigPersistence.getMaxPeriod())

    /* Playback rate */
    val minRate: IntegerProperty = intProperty(ConfigPersistence.getSoundTriggerMinRate(type))
    val maxRate: IntegerProperty = intProperty(ConfigPersistence.getSoundTriggerMaxRate(type))

    init {
        enabled.onChange { ConfigPersistence.toggleSoundTrigger(type, it) }
        bountyRuneTimer.onChange { ConfigPersistence.setBountyRuneTimer(it) }
        useSmartChance.onChange { ConfigPersistence.setIsUsingHealSmartChance(it) }
        chance.onChange { ConfigPersistence.setSoundTriggerChance(type, it) }
        minPeriod.onChange {
            ConfigPersistence.setMinPeriod(it)
            if (it > maxPeriod.get()) {
                maxPeriod.set(it)
            }
        }
        maxPeriod.onChange {
            ConfigPersistence.setMaxPeriod(it)
            if (it < minPeriod.get()) {
                minPeriod.set(it)
            }
        }
        minRate.onChange {
            ConfigPersistence.setSoundTriggerMinRate(type, it)
            if (it > maxRate.get()) {
                maxRate.set(it)
            }
        }
        maxRate.onChange {
            ConfigPersistence.setSoundTriggerMaxRate(type, it)
            if (it < minRate.get()) {
                minRate.set(it)
            }
        }
    }

    fun onChooseSoundsClicked() {
        find<ChooseSoundFilesScreen>(ChooseSoundFilesScreen.params(type))
            .openModal(block = true, resizable = false)
        soundBiteCount.set(ConfigPersistence.getSoundsForType(type).size.toString())
    }
}