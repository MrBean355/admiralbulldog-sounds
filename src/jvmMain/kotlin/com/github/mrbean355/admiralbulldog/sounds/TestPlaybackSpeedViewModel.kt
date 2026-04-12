package com.github.mrbean355.admiralbulldog.sounds

import com.github.mrbean355.admiralbulldog.arch.ComposeViewModel
import com.github.mrbean355.admiralbulldog.assets.SoundBite
import com.github.mrbean355.admiralbulldog.assets.SoundBites
import com.github.mrbean355.admiralbulldog.common.DEFAULT_RATE
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class TestPlaybackSpeedViewModel : ComposeViewModel() {
    private val allSounds = SoundBites.getAll()

    private val _playbackRate = MutableStateFlow(DEFAULT_RATE)
    val playbackRate: StateFlow<Int> = _playbackRate.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val filteredSounds: StateFlow<List<SoundBite>> = combine(searchQuery, _playbackRate) { query, _ ->
        allSounds.filter { it.name.contains(query.trim(), ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, allSounds)

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onRateChanged(rate: Int) {
        _playbackRate.value = rate
    }
}