package com.github.mrbean355.admiralbulldog.sounds.combo

import com.github.mrbean355.admiralbulldog.arch.ComposeViewModel
import com.github.mrbean355.admiralbulldog.assets.ComboSoundBite
import com.github.mrbean355.admiralbulldog.assets.SoundBite
import com.github.mrbean355.admiralbulldog.assets.SoundBites
import com.github.mrbean355.admiralbulldog.assets.playAll
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.common.showError
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CreateSoundComboViewModel(private val originalSound: ComboSoundBite?) : ComposeViewModel() {
    private var lastQuery = ""

    private val _name = MutableStateFlow(originalSound?.name.orEmpty())
    val name: StateFlow<String> = _name.asStateFlow()

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _selectedSoundBite = MutableStateFlow<SoundBite?>(null)
    val selectedSoundBite: StateFlow<SoundBite?> = _selectedSoundBite.asStateFlow()

    private val _items = MutableStateFlow<List<SoundBite>>(originalSound?.getSoundBites().orEmpty())
    val items: StateFlow<List<SoundBite>> = _items.asStateFlow()

    fun onNameChanged(newName: String) {
        _name.value = newName
    }

    fun onQueryChanged(text: String) {
        _query.value = text
        val isDeleting = text.length < lastQuery.length
        if (!isDeleting && text.isNotEmpty()) {
            val match = SoundBites.getSingleSoundBites().singleOrNull { it.name.startsWith(text, ignoreCase = true) }
            if (match != null) {
                _query.value = match.name
            }
        }
        _selectedSoundBite.value = SoundBites.getSingleSoundBites().find { it.name == _query.value }
        lastQuery = _query.value
    }

    fun onAddClicked() {
        _selectedSoundBite.value?.let {
            _items.value = _items.value + it
            _query.value = ""
            _selectedSoundBite.value = null
        }
    }

    fun onRemoveClicked(index: Int) {
        val current = _items.value.toMutableList()
        if (index in current.indices) {
            current.removeAt(index)
            _items.value = current
        }
    }

    fun onPlayClicked() {
        viewModelScope.launch {
            _items.value.playAll()
        }
    }

    fun onSaveClicked() {
        val newName = _name.value.trim()
        if (newName.isEmpty()) return

        if (originalSound == null) {
            if (ConfigPersistence.hasSoundCombo(newName)) {
                showError(getString("header_sound_combo_exists"), getString("content_sound_combo_exists", newName))
                return
            }
        } else {
            if (newName != originalSound.name && ConfigPersistence.hasSoundCombo(newName)) {
                showError(getString("header_sound_combo_exists"), getString("content_sound_combo_exists", newName))
                return
            }
            ConfigPersistence.removeSoundCombo(originalSound)
        }
        ConfigPersistence.saveSoundCombo(newName, _items.value)
        SoundBites.refreshCombos()
        requestWindowClose()
    }
}