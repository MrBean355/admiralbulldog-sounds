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

package com.github.mrbean355.admiralbulldog.discord

import com.github.mrbean355.admiralbulldog.arch.AppViewModel
import com.github.mrbean355.admiralbulldog.arch.repo.DiscordBotRepository
import com.github.mrbean355.admiralbulldog.common.GreenDotIcon
import com.github.mrbean355.admiralbulldog.common.GreyDotIcon
import com.github.mrbean355.admiralbulldog.common.RedDotIcon
import com.github.mrbean355.admiralbulldog.common.YellowDotIcon
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import com.github.mrbean355.admiralbulldog.triggers.SOUND_TRIGGER_TYPES
import com.github.mrbean355.admiralbulldog.triggers.SoundTriggerType
import javafx.beans.binding.Binding
import javafx.beans.binding.StringBinding
import javafx.beans.property.BooleanProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.StringProperty
import javafx.scene.image.Image
import kotlinx.coroutines.launch
import tornadofx.booleanProperty
import tornadofx.objectBinding
import tornadofx.objectProperty
import tornadofx.onChange
import tornadofx.stringBinding
import tornadofx.stringProperty

class DiscordBotViewModel : AppViewModel() {
    private val discordBotRepository = DiscordBotRepository()
    private val toggles: Map<SoundTriggerType, BooleanProperty>
    private val lookupResponse = stringProperty()
    private val statusType: ObjectProperty<Status> = objectProperty()

    val botEnabled: BooleanProperty = booleanProperty(ConfigPersistence.isUsingDiscordBot())
    val token: StringProperty = stringProperty(ConfigPersistence.getDiscordToken())
    val statusImage: Binding<Image?> = statusType.objectBinding { it?.getImage() }
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

    private enum class Status {
        NEUTRAL,
        GOOD,
        BAD,
        LOADING;

        fun getImage(): Image = when (this) {
            NEUTRAL -> GreyDotIcon()
            GOOD -> GreenDotIcon()
            BAD -> RedDotIcon()
            LOADING -> YellowDotIcon()
        }
    }
}