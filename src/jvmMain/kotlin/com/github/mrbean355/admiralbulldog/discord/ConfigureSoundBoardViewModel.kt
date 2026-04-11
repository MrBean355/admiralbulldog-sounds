package com.github.mrbean355.admiralbulldog.discord

import com.github.mrbean355.admiralbulldog.arch.ComposeViewModel
import com.github.mrbean355.admiralbulldog.assets.SoundBite
import com.github.mrbean355.admiralbulldog.assets.SoundBites
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class ConfigureSoundBoardViewModel : ComposeViewModel() {
    private val allItems = SoundBites.getAll()

    val searchQuery = MutableStateFlow("")
    val soundToggles = MutableStateFlow(
        allItems.associateWith { it in ConfigPersistence.getSoundBoard() }
    )

    val filteredItems: StateFlow<List<SoundBite>> = combine(searchQuery, soundToggles) { query, toggles ->
        val trimmed = query.trim()
        val matching = if (trimmed.isEmpty()) {
            allItems
        } else {
            allItems.filter { it.name.contains(trimmed, ignoreCase = true) }
        }

        matching.sortedWith { lhs, rhs ->
            val lhsSelected = toggles[lhs] ?: false
            val rhsSelected = toggles[rhs] ?: false
            when {
                lhsSelected == rhsSelected -> lhs.name.compareTo(rhs.name, ignoreCase = true)
                lhsSelected -> -1
                else -> 1
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun toggleSound(sound: SoundBite, checked: Boolean) {
        soundToggles.value = soundToggles.value.toMutableMap().apply {
            put(sound, checked)
        }
    }

    fun onSelectAll() {
        soundToggles.value = soundToggles.value.keys.associateWith { true }
    }

    fun onDeselectAll() {
        soundToggles.value = soundToggles.value.keys.associateWith { false }
    }

    fun onPlayClicked(soundBite: SoundBite) {
        soundBite.play()
    }

    fun onSave(onComplete: () -> Unit) {
        val selection = soundToggles.value.filterValues { it }.keys.toList()
        ConfigPersistence.setSoundBoard(selection)
        onComplete()
        requestWindowClose()
    }
}
