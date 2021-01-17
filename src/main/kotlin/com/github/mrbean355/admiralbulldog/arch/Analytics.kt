/*
 * Copyright 2021 Michael Johnston
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

package com.github.mrbean355.admiralbulldog.arch

import com.github.mrbean355.admiralbulldog.APP_VERSION
import com.github.mrbean355.admiralbulldog.DISTRIBUTION
import com.github.mrbean355.admiralbulldog.arch.repo.DiscordBotRepository
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import com.github.mrbean355.admiralbulldog.triggers.SOUND_TRIGGER_TYPES

private val discordBotRepository = DiscordBotRepository()

suspend fun logAnalyticsProperties() {
    val enabled = ConfigPersistence.getEnabledSoundTriggers()
    val triggers = SOUND_TRIGGER_TYPES.map {
        "sounds.triggers.${it.simpleName.orEmpty().decapitalize()}" to (it in enabled)
    }
    discordBotRepository.logAnalyticsProperties(mapOf(
            "app.version" to APP_VERSION,
            "app.distribution" to DISTRIBUTION,
            "app.update" to ConfigPersistence.getAppUpdateFrequency(),
            "sounds.update" to ConfigPersistence.getSoundsUpdateFrequency(),
            "tray.enabled" to ConfigPersistence.isMinimizeToTray(),
            "tray.permanent" to ConfigPersistence.isAlwaysShowTrayIcon(),
            "bot.enabled" to ConfigPersistence.isUsingDiscordBot(),
            "mod.update" to ConfigPersistence.getModUpdateFrequency(),
            "mod.selection" to ConfigPersistence.getEnabledMods().joinToString(separator = ",")
    ) + triggers)
}