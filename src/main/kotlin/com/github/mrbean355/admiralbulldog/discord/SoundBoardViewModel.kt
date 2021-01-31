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

package com.github.mrbean355.admiralbulldog.discord

import com.github.mrbean355.admiralbulldog.arch.AppViewModel
import com.github.mrbean355.admiralbulldog.arch.repo.DiscordBotRepository
import com.github.mrbean355.admiralbulldog.assets.SoundBite
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.common.showError
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import javafx.beans.property.BooleanProperty
import javafx.beans.property.IntegerProperty
import javafx.collections.ObservableList
import javafx.scene.control.ButtonType
import kotlinx.coroutines.launch
import tornadofx.booleanProperty
import tornadofx.intProperty
import tornadofx.onChange
import tornadofx.toObservable

class SoundBoardViewModel : AppViewModel() {
    private val discordBotRepository = DiscordBotRepository()

    val playbackRate: IntegerProperty = intProperty(ConfigPersistence.getSoundBoardRate())
    val soundBoard: ObservableList<SoundBite> = ConfigPersistence.getSoundBoard().toObservable()
    val isEmpty: BooleanProperty = booleanProperty()

    init {
        playbackRate.onChange(ConfigPersistence::setSoundBoardRate)
    }

    fun refresh() {
        soundBoard.setAll(ConfigPersistence.getSoundBoard())
        isEmpty.set(soundBoard.isEmpty())
    }

    fun onSoundClicked(soundBite: SoundBite) {
        viewModelScope.launch {
            val response = discordBotRepository.playSound(soundBite, playbackRate.value)
            if (!response.isSuccessful()) {
                showError(getString("header_discord_sound_failed"), getString("content_discord_sound_failed"), ButtonType.OK)
            }
        }
    }
}
