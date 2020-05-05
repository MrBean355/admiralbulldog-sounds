package com.github.mrbean355.admiralbulldog.ui2.discord

import com.github.mrbean355.admiralbulldog.arch.DiscordBotRepository
import com.github.mrbean355.admiralbulldog.ui.getString
import com.github.mrbean355.admiralbulldog.ui2.persistance.AppConfig
import com.github.mrbean355.admiralbulldog.ui2.triggers.SoundTrigger
import javafx.beans.property.BooleanProperty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import tornadofx.FXEvent
import tornadofx.ViewModel
import tornadofx.asObservable

class DiscordBotViewModel : ViewModel() {
    private val discordBotRepository = DiscordBotRepository()
    private val coroutineScope = CoroutineScope(IO) + SupervisorJob()

    val items = SoundTrigger.getAll().asObservable()
    val enabledProperty = AppConfig.discordBotEnabledProperty()
    val magicNumberProperty = AppConfig.discordMagicNumberProperty()

    fun getPlayThroughDiscordProperty(trigger: SoundTrigger): BooleanProperty {
        return AppConfig.playThroughDiscordProperty(trigger)
    }

    fun onTestClicked() {
        coroutineScope.launch {
            val response = discordBotRepository.lookUpToken(AppConfig.discordMagicNumberProperty().get())
            val body = response.body
            if (response.isSuccessful() && body != null) {
                fire(SuccessEvent(getString("content_valid_magic_number", body)))
            } else {
                fire(ErrorEvent(getString("content_invalid_magic_number")))
            }
        }
    }

    class SuccessEvent(val message: String) : FXEvent()
    class ErrorEvent(val message: String) : FXEvent()
}
