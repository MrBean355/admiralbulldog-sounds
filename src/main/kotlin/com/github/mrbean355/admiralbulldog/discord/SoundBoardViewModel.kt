package com.github.mrbean355.admiralbulldog.discord

import com.github.mrbean355.admiralbulldog.arch.AppViewModel
import com.github.mrbean355.admiralbulldog.arch.repo.DiscordBotRepository
import com.github.mrbean355.admiralbulldog.assets.SoundBite
import com.github.mrbean355.admiralbulldog.common.error
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import javafx.beans.property.BooleanProperty
import javafx.collections.ObservableList
import javafx.scene.control.ButtonType
import kotlinx.coroutines.launch
import tornadofx.*

class SoundBoardViewModel : AppViewModel() {
    private val discordBotRepository = DiscordBotRepository()

    val soundBoard: ObservableList<SoundBite> = ConfigPersistence.getSoundBoard().toObservable()
    val isEmpty: BooleanProperty = booleanProperty()

    fun refresh() {
        soundBoard.setAll(ConfigPersistence.getSoundBoard())
        isEmpty.set(soundBoard.isEmpty())
    }

    fun onSoundClicked(soundBite: SoundBite) {
        viewModelScope.launch {
            val response = discordBotRepository.playSound(soundBite)
            if (!response.isSuccessful()) {
                error(getString("header_discord_sound_failed"), getString("content_discord_sound_failed"), ButtonType.OK)
            }
        }
    }
}
