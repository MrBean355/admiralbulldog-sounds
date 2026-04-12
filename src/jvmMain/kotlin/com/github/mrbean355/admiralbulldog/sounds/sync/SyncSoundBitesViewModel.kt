package com.github.mrbean355.admiralbulldog.sounds.sync

import com.github.mrbean355.admiralbulldog.arch.ComposeViewModel
import com.github.mrbean355.admiralbulldog.assets.SoundBites
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SyncSoundBitesViewModel : ComposeViewModel() {
    private val _isFinished = MutableStateFlow(false)
    val isFinished: StateFlow<Boolean> = _isFinished.asStateFlow()

    private val _progress = MutableStateFlow(0f)
    val progress: StateFlow<Float> = _progress.asStateFlow()

    private val _syncResult = MutableStateFlow<SoundBites.SyncResult?>(null)
    val syncResult: StateFlow<SoundBites.SyncResult?> = _syncResult.asStateFlow()

    private val _showError = MutableStateFlow(false)
    val showError: StateFlow<Boolean> = _showError.asStateFlow()

    val showNoUpdates = MutableSharedFlow<Unit>()

    init {
        viewModelScope.launch {
            SoundBites.progress.collect { value ->
                _progress.value = value.toFloat()
            }
        }
        updateSounds()
    }

    fun updateSounds() {
        _showError.value = false
        viewModelScope.launch {
            val result = SoundBites.synchronise()
            if (result == null) {
                _showError.value = true
                return@launch
            }
            val updateCount = with(result) { newSounds.size + changedSounds.size + deletedSounds.size + failedSounds.size }
            if (updateCount == 0) {
                ConfigPersistence.setSoundsLastUpdateToNow()
                showNoUpdates.emit(Unit)
                requestWindowClose()
                return@launch
            }
            _syncResult.value = result
            _isFinished.value = true
            if (result.failedSounds.isEmpty()) {
                ConfigPersistence.setSoundsLastUpdateToNow()
            }
            SoundBites.checkForInvalidSounds()
        }
    }

    fun onCancelClicked() {
        requestWindowClose()
    }

    fun onDoneClicked() {
        requestWindowClose()
    }

    fun onErrorDismissed() {
        _showError.value = false
        requestWindowClose()
    }
}