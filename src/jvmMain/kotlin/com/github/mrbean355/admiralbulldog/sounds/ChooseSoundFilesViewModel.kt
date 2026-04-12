package com.github.mrbean355.admiralbulldog.sounds

import com.github.mrbean355.admiralbulldog.arch.ComposeViewModel
import com.github.mrbean355.admiralbulldog.assets.SoundBite
import com.github.mrbean355.admiralbulldog.assets.SoundBites
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import com.github.mrbean355.admiralbulldog.triggers.SoundTriggerType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class ChooseSoundFilesViewModel(val type: SoundTriggerType) : ComposeViewModel() {
    private val allSounds = SoundBites.getAll()
    private val initialSelection = ConfigPersistence.getSoundsForType(type)

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedSounds = MutableStateFlow(initialSelection.toSet())
    val selectedSounds: StateFlow<Set<SoundBite>> = _selectedSounds.asStateFlow()

    val filteredSounds: StateFlow<List<SoundBite>> = combine(searchQuery, selectedSounds) { query, selected ->
        allSounds.filter { it.name.contains(query.trim(), ignoreCase = true) }
            .sortedWith(compareByDescending<SoundBite> { it in selected }.thenBy { it.name })
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun toggleSound(sound: SoundBite, isSelected: Boolean) {
        if (isSelected) {
            _selectedSounds.value += sound
        } else {
            _selectedSounds.value -= sound
        }
    }

    fun selectAll() {
        _selectedSounds.value = allSounds.toSet()
    }

    fun selectNone() {
        _selectedSounds.value = emptySet()
    }

    fun onSave(onSaved: () -> Unit) {
        ConfigPersistence.saveSoundsForType(type, _selectedSounds.value.toList())
        onSaved()
        requestWindowClose()
    }
}
