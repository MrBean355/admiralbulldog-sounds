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

package com.github.mrbean355.admiralbulldog.arch

import com.github.mrbean355.admiralbulldog.APP_VERSION
import com.github.mrbean355.admiralbulldog.DISTRIBUTION
import com.github.mrbean355.admiralbulldog.arch.repo.DiscordBotRepository
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import com.github.mrbean355.admiralbulldog.triggers.SOUND_TRIGGER_TYPES
import com.github.mrbean355.admiralbulldog.triggers.SoundTriggerType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private val discordBotRepository = DiscordBotRepository()

suspend fun logAnalyticsProperties(): Unit = withContext(Dispatchers.IO) {
    val enabledTriggers = ConfigPersistence.getEnabledSoundTriggers()
    val discordTriggers = SOUND_TRIGGER_TYPES.filter(ConfigPersistence::isPlayedThroughDiscord)

    discordBotRepository.logAnalyticsProperties(
        mapOf(
            "app.version" to APP_VERSION,
            "app.distribution" to DISTRIBUTION,
            "app.update" to ConfigPersistence.getAppUpdateFrequency(),
            "app.darkMode" to ConfigPersistence.isDarkMode(),
            "tray.enabled" to ConfigPersistence.isMinimizeToTray(),
            "tray.permanent" to ConfigPersistence.isAlwaysShowTrayIcon(),
            "sounds.update" to ConfigPersistence.getSoundsUpdateFrequency(),
            "sounds.triggers.selection" to enabledTriggers.toAnalyticsString(),
            "sounds.triggers.onHeal.useSmartChance" to ConfigPersistence.isUsingHealSmartChance(),
            "sounds.triggers.periodically.min" to ConfigPersistence.getMinPeriod(),
            "sounds.triggers.periodically.max" to ConfigPersistence.getMaxPeriod(),
            "sounds.triggers.onBountyRunesSpawn.timer" to ConfigPersistence.getBountyRuneTimer(),
            "sounds.triggers.onWisdomRunesSpawn.timer" to ConfigPersistence.getWisdomRuneTimer(),
            "sounds.volumes.size" to ConfigPersistence.getSoundBiteVolumes().size,
            "sounds.combos.size" to ConfigPersistence.getSoundCombos().size,
            "bot.enabled" to ConfigPersistence.isUsingDiscordBot(),
            "bot.triggers.selection" to discordTriggers.toAnalyticsString(),
            "bot.soundBoard.size" to ConfigPersistence.getSoundBoard().size,
            "mod.update" to ConfigPersistence.getModUpdateFrequency(),
            "mod.selection" to ConfigPersistence.getEnabledMods().joinToString(separator = ","),
        )
    )
}

private fun Iterable<SoundTriggerType>.toAnalyticsString(): String {
    return map { it.simpleName.orEmpty().replaceFirstChar(Char::lowercase) }
        .sorted()
        .joinToString(separator = ",")
}