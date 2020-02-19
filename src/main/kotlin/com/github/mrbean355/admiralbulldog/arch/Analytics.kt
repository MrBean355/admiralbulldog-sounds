package com.github.mrbean355.admiralbulldog.arch

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

private val discordBotRepository = DiscordBotRepository()

fun logAnalyticsEvent(eventType: String, eventData: String = "") {
    GlobalScope.launch {
        discordBotRepository.logAnalyticsEvent(eventType, eventData)
    }
}