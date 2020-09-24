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
            "mod.enabled" to ConfigPersistence.isModEnabled(),
            "mod.version" to ConfigPersistence.getModVersion(),
            "mod.update" to ConfigPersistence.getModUpdateFrequency()
    ) + triggers)
}