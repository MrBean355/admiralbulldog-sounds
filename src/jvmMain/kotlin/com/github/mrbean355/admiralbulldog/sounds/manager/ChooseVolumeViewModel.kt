package com.github.mrbean355.admiralbulldog.sounds.manager

import com.github.mrbean355.admiralbulldog.arch.ComposeViewModel
import com.github.mrbean355.admiralbulldog.assets.SoundBite
import com.github.mrbean355.admiralbulldog.assets.SoundBites
import com.github.mrbean355.admiralbulldog.common.DEFAULT_INDIVIDUAL_VOLUME
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ChooseVolumeViewModel(initialName: String) : ComposeViewModel() {
    private var lastText = initialName

    private val _query = MutableStateFlow(initialName)
    val query: StateFlow<String> = _query.asStateFlow()

    private val _volume = MutableStateFlow(ConfigPersistence.getSoundBiteVolume(initialName) ?: DEFAULT_INDIVIDUAL_VOLUME)
    val volume: StateFlow<Int> = _volume.asStateFlow()

    private val _selectedSoundBite = MutableStateFlow<SoundBite?>(null)
    val selectedSoundBite: StateFlow<SoundBite?> = _selectedSoundBite.asStateFlow()

    init {
        if (initialName.isNotEmpty()) {
            onQueryChanged(initialName)
        }
    }

    fun onQueryChanged(text: String) {
        _query.value = text
        val isDeleting = text.length < lastText.length
        if (!isDeleting && text.isNotEmpty()) {
            val match = SoundBites.getSingleSoundBites().singleOrNull { it.name.startsWith(text, ignoreCase = true) }
            if (match != null) {
                _query.value = match.name
            }
        }
        _selectedSoundBite.value = SoundBites.getSingleSoundBites().find { it.name == _query.value }
        lastText = _query.value
    }

    fun onVolumeChanged(newVolume: Int) {
        _volume.value = newVolume
    }

    fun onPlayClicked() {
        _selectedSoundBite.value?.play(volume = _volume.value)
    }

    fun onDoneClicked() {
        _selectedSoundBite.value?.let {
            ConfigPersistence.addSoundBiteVolume(it.name, _volume.value)
            requestWindowClose()
        }
    }
}