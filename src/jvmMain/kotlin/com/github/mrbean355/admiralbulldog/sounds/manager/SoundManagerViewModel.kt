package com.github.mrbean355.admiralbulldog.sounds.manager

import com.github.mrbean355.admiralbulldog.arch.ComposeViewModel
import com.github.mrbean355.admiralbulldog.assets.SoundBite
import com.github.mrbean355.admiralbulldog.assets.SoundBites
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import com.github.mrbean355.admiralbulldog.sounds.combo.openSoundCombosScreen
import com.github.mrbean355.admiralbulldog.triggers.SOUND_TRIGGER_TYPES
import com.github.mrbean355.admiralbulldog.triggers.SoundTriggerType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SoundManagerViewModel : ComposeViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _showPlaySounds = MutableStateFlow(true)
    val showPlaySounds: StateFlow<Boolean> = _showPlaySounds.asStateFlow()

    private val _showCombos = MutableStateFlow(true)
    val showCombos: StateFlow<Boolean> = _showCombos.asStateFlow()

    private val _showUsed = MutableStateFlow(true)
    val showUsed: StateFlow<Boolean> = _showUsed.asStateFlow()

    private val _showUnused = MutableStateFlow(true)
    val showUnused: StateFlow<Boolean> = _showUnused.asStateFlow()

    private val _items = MutableStateFlow<List<SoundBite>>(emptyList())
    val items: StateFlow<List<SoundBite>> = _items.asStateFlow()

    init {
        refreshDisplayedSoundBites()
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        refreshDisplayedSoundBites()
    }

    fun onFilterChanged(filter: Filter, value: Boolean) {
        when (filter) {
            Filter.PLAY_SOUNDS -> _showPlaySounds.value = value
            Filter.COMBOS -> _showCombos.value = value
            Filter.USED -> _showUsed.value = value
            Filter.UNUSED -> _showUnused.value = value
        }
        refreshDisplayedSoundBites()
    }

    fun isTriggerSelected(type: SoundTriggerType, sound: SoundBite): Boolean {
        return ConfigPersistence.isSoundSelectedForType(type, sound)
    }

    fun onToggleTrigger(type: SoundTriggerType, sound: SoundBite, checked: Boolean) {
        ConfigPersistence.setSoundSelectedForType(type, sound, checked)
        // No need to refresh entire list unless filters depend on "used/unused" state changing
        if (!_showUsed.value || !_showUnused.value) {
            refreshDisplayedSoundBites()
        }
    }

    fun onManageVolumesClicked() {
        openVolumeManagerScreen()
    }

    fun onSoundCombosClicked() {
        openSoundCombosScreen {
            refreshDisplayedSoundBites()
        }
    }

    private fun refreshDisplayedSoundBites() {
        val newItems = mutableListOf<SoundBite>()
        if (_showPlaySounds.value) {
            newItems += SoundBites.getSingleSoundBites()
        }
        if (_showCombos.value) {
            newItems += SoundBites.getComboSoundBites()
        }
        if (!_showUsed.value) {
            newItems.removeAll { it.isUsed() }
        }
        if (!_showUnused.value) {
            newItems.removeAll { !it.isUsed() }
        }
        val query = _searchQuery.value.trim()
        _items.value = newItems
            .filter { it.name.contains(query, ignoreCase = true) }
            .sortedBy { it.name.lowercase() }
    }

    private fun SoundBite.isUsed(): Boolean = SOUND_TRIGGER_TYPES.any {
        ConfigPersistence.isSoundSelectedForType(it, this)
    }

    enum class Filter {
        PLAY_SOUNDS, COMBOS, USED, UNUSED
    }
}