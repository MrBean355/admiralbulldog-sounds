package com.github.mrbean355.admiralbulldog.discord

import com.github.mrbean355.admiralbulldog.arch.AppViewModel
import com.github.mrbean355.admiralbulldog.arch.repo.DiscordBotRepository
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import com.github.mrbean355.admiralbulldog.triggers.SOUND_TRIGGER_TYPES
import com.github.mrbean355.admiralbulldog.triggers.SoundTriggerType
import javafx.beans.binding.StringBinding
import javafx.beans.property.BooleanProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.StringProperty
import kotlinx.coroutines.launch
import tornadofx.*

class DiscordBotViewModel : AppViewModel() {
    private val discordBotRepository = DiscordBotRepository()
    private val toggles: Map<SoundTriggerType, BooleanProperty>
    private val lookupResponse = stringProperty()
    private val statusType: ObjectProperty<Status> = objectProperty()

    val botEnabled: BooleanProperty = booleanProperty(ConfigPersistence.isUsingDiscordBot())
    val token: StringProperty = stringProperty(ConfigPersistence.getDiscordToken())
    val statusImage: StringBinding = statusType.stringBinding { it?.image }
    val status: StringBinding = botEnabled.stringBinding(lookupResponse) {
        if (it == true) {
            lookupResponse.get()
        } else {
            statusType.set(Status.NEUTRAL)
            getString("msg_bot_disabled")
        }
    }

    init {
        toggles = SOUND_TRIGGER_TYPES.associateWith { type ->
            booleanProperty(ConfigPersistence.isPlayedThroughDiscord(type)).apply {
                onChange { ConfigPersistence.setPlayedThroughDiscord(type, it) }
            }
        }
        botEnabled.onChange {
            ConfigPersistence.setUsingDiscordBot(it)
            if (it) {
                lookupDiscordGuild()
            }
        }
        token.onChange {
            ConfigPersistence.setDiscordToken(it.orEmpty())
            lookupDiscordGuild()
        }
    }

    override fun onReady() {
        if (botEnabled.get()) {
            lookupDiscordGuild()
        } else {
            statusType.set(Status.NEUTRAL)
        }
    }

    fun throughDiscordProperty(type: SoundTriggerType): BooleanProperty {
        return toggles.getValue(type)
    }

    private fun lookupDiscordGuild() {
        statusType.set(Status.LOADING)
        lookupResponse.set(getString("msg_bot_loading"))

        viewModelScope.launch {
            val response = discordBotRepository.lookUpToken(token.get())
            if (response.isSuccessful()) {
                statusType.set(Status.GOOD)
                lookupResponse.set(getString("msg_bot_active", response.body))
            } else {
                statusType.set(Status.BAD)
                if (response.statusCode == 404) {
                    lookupResponse.set(getString("msg_bot_not_found"))
                } else {
                    lookupResponse.set(getString("msg_bot_error"))
                }
            }
        }
    }

    private enum class Status(val image: String) {
        NEUTRAL("grey_dot.png"),
        GOOD("green_dot.png"),
        BAD("red_dot.png"),
        LOADING("yellow_dot.png")
    }
}