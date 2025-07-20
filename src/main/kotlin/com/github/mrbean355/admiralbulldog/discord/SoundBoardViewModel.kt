package com.github.mrbean355.admiralbulldog.discord

import com.github.mrbean355.admiralbulldog.arch.AppViewModel
import com.github.mrbean355.admiralbulldog.arch.repo.DiscordBotRepository
import com.github.mrbean355.admiralbulldog.assets.SoundBite
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.common.showError
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import javafx.beans.binding.StringBinding
import javafx.beans.property.BooleanProperty
import javafx.beans.property.IntegerProperty
import javafx.beans.property.StringProperty
import javafx.collections.ObservableList
import javafx.scene.control.ButtonType
import kotlinx.coroutines.launch
import tornadofx.booleanProperty
import tornadofx.intProperty
import tornadofx.onChange
import tornadofx.stringBinding
import tornadofx.stringProperty
import tornadofx.toObservable

class SoundBoardViewModel : AppViewModel() {
    private val discordBotRepository = DiscordBotRepository()

    val playbackRate: IntegerProperty = intProperty(ConfigPersistence.getSoundBoardRate())
    val soundBoard: ObservableList<SoundBite> = ConfigPersistence.getSoundBoard().toObservable()
    val isEmpty: BooleanProperty = booleanProperty(soundBoard.isEmpty())
    val searchQuery: StringProperty = stringProperty()
    val emptyMessage: StringBinding = searchQuery.stringBinding {
        getString(if (it.isNullOrBlank()) "label_sound_board_empty" else "label_sound_board_empty_no_matches")
    }

    init {
        playbackRate.onChange(ConfigPersistence::setSoundBoardRate)
        searchQuery.onChange { refresh() }
    }

    fun refresh() {
        val query = searchQuery.value.orEmpty().trim()
        soundBoard.setAll(ConfigPersistence.getSoundBoard().filter { it.name.contains(query, ignoreCase = true) })
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
