package com.github.mrbean355.admiralbulldog.discord

import com.github.mrbean355.admiralbulldog.arch.ComposeViewModel
import com.github.mrbean355.admiralbulldog.arch.repo.DiscordBotRepository
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import com.github.mrbean355.admiralbulldog.triggers.SOUND_TRIGGER_TYPES
import com.github.mrbean355.admiralbulldog.triggers.SoundTriggerType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DiscordBotViewModel : ComposeViewModel() {
    private val discordBotRepository = DiscordBotRepository()
    private val triggerToggles: Map<SoundTriggerType, MutableStateFlow<Boolean>>
    private val lookupResponse = MutableStateFlow("")
    private val _statusType = MutableStateFlow(Status.NEUTRAL)

    val botEnabled = MutableStateFlow(ConfigPersistence.isUsingDiscordBot())
    val token = MutableStateFlow(ConfigPersistence.getDiscordToken())
    val statusType: StateFlow<Status> = _statusType

    val status: StateFlow<String> = combine(botEnabled, lookupResponse) { enabled, response ->
        if (enabled) {
            response
        } else {
            _statusType.value = Status.NEUTRAL
            getString("msg_bot_disabled")
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, "")

    init {
        triggerToggles = SOUND_TRIGGER_TYPES.associateWith { type ->
            MutableStateFlow(ConfigPersistence.isPlayedThroughDiscord(type))
        }

        viewModelScope.launch {
            botEnabled.collect {
                ConfigPersistence.setUsingDiscordBot(it)
                if (it) {
                    lookupDiscordGuild()
                }
            }
        }

        viewModelScope.launch {
            token.collect {
                ConfigPersistence.setDiscordToken(it)
                if (botEnabled.value) {
                    lookupDiscordGuild()
                }
            }
        }

        triggerToggles.forEach { (type, flow) ->
            viewModelScope.launch {
                flow.collect {
                    ConfigPersistence.setPlayedThroughDiscord(type, it)
                }
            }
        }

        if (botEnabled.value) {
            lookupDiscordGuild()
        }
    }

    fun getTriggerToggle(type: SoundTriggerType): MutableStateFlow<Boolean> {
        return triggerToggles.getValue(type)
    }

    private fun lookupDiscordGuild() {
        _statusType.value = Status.LOADING
        lookupResponse.value = getString("msg_bot_loading")

        viewModelScope.launch {
            val response = discordBotRepository.lookUpToken(token.value)
            if (response.isSuccessful()) {
                _statusType.value = Status.GOOD
                lookupResponse.value = getString("msg_bot_active", response.body)
            } else {
                _statusType.value = Status.BAD
                if (response.statusCode == 404) {
                    lookupResponse.value = getString("msg_bot_not_found")
                } else {
                    lookupResponse.value = getString("msg_bot_error")
                }
            }
        }
    }

    enum class Status {
        NEUTRAL,
        GOOD,
        BAD,
        LOADING
    }
}