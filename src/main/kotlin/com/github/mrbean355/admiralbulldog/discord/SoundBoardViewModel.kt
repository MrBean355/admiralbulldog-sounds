package com.github.mrbean355.admiralbulldog.discord

import com.github.mrbean355.admiralbulldog.arch.AppViewModel
import com.github.mrbean355.admiralbulldog.arch.DiscordBotRepository
import com.github.mrbean355.admiralbulldog.assets.SoundBite
import com.github.mrbean355.admiralbulldog.common.error
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import javafx.beans.property.BooleanProperty
import javafx.collections.ObservableList
import javafx.scene.control.ButtonType
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tornadofx.booleanProperty
import tornadofx.toObservable

class SoundBoardViewModel : AppViewModel() {
    private val discordBotRepository = DiscordBotRepository()

    val soundBoard: ObservableList<SoundBite> = ConfigPersistence.getSoundBoard().toObservable()
    val isEmpty: BooleanProperty = booleanProperty()

    fun refresh() {
        soundBoard.setAll(ConfigPersistence.getSoundBoard())
        isEmpty.set(soundBoard.isEmpty())
    }

    fun onSoundClicked(soundBite: SoundBite) {
        coroutineScope.launch {
            val response = discordBotRepository.playSound(soundBite)
            if (!response.isSuccessful()) {
                withContext(Main) {
                    error(getString("header_discord_sound_failed"), getString("content_discord_sound_failed"), ButtonType.OK)
                }
            }
        }
    }
}
