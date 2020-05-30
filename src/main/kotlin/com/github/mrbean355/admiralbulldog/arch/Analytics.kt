package com.github.mrbean355.admiralbulldog.arch

import com.github.mrbean355.admiralbulldog.arch.repo.DiscordBotRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

private val discordBotRepository = DiscordBotRepository()

const val EVENT_TYPE_CLICK = "button_click"

fun logAnalyticsEvent(eventType: String, eventData: String = "") {
    GlobalScope.launch {
        discordBotRepository.logAnalyticsEvent(eventType, eventData)
    }
}