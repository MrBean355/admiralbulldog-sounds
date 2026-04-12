package com.github.mrbean355.admiralbulldog.discord

import com.github.mrbean355.admiralbulldog.arch.ComposeViewModel
import com.github.mrbean355.admiralbulldog.arch.repo.DiscordBotRepository
import com.github.mrbean355.admiralbulldog.assets.SoundBite
import com.github.mrbean355.admiralbulldog.common.AlertButton
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.common.showError
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SoundBoardViewModel : ComposeViewModel() {
    private val discordBotRepository = DiscordBotRepository()

    val playbackRate = MutableStateFlow(ConfigPersistence.getSoundBoardRate())
    val searchQuery = MutableStateFlow("")

    private val allSoundBoardItems = MutableStateFlow(ConfigPersistence.getSoundBoard())

    val soundBoard: StateFlow<List<SoundBite>> = combine(allSoundBoardItems, searchQuery) { items, query ->
        val trimmed = query.trim()
        if (trimmed.isEmpty()) items else items.filter { it.name.contains(trimmed, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val isEmpty: StateFlow<Boolean> = soundBoard.map { it.isEmpty() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, allSoundBoardItems.value.isEmpty())

    val emptyMessage: StateFlow<String> = searchQuery.map { query ->
        getString(if (query.isBlank()) "label_sound_board_empty" else "label_sound_board_empty_no_matches")
    }.stateIn(viewModelScope, SharingStarted.Eagerly, "")

    init {
        viewModelScope.launch {
            playbackRate.collect {
                ConfigPersistence.setSoundBoardRate(it)
            }
        }
    }

    fun refresh() {
        allSoundBoardItems.value = ConfigPersistence.getSoundBoard()
    }

    fun onSoundClicked(soundBite: SoundBite) {
        viewModelScope.launch {
            val response = discordBotRepository.playSound(soundBite, playbackRate.value)
            if (!response.isSuccessful()) {
                showError(getString("header_discord_sound_failed"), getString("content_discord_sound_failed"), AlertButton.OK)
            }
        }
    }
}
