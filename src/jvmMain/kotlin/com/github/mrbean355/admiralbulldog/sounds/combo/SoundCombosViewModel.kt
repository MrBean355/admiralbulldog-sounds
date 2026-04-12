package com.github.mrbean355.admiralbulldog.sounds.combo

import com.github.mrbean355.admiralbulldog.arch.ComposeViewModel
import com.github.mrbean355.admiralbulldog.assets.ComboSoundBite
import com.github.mrbean355.admiralbulldog.assets.SoundBites
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.common.showInformation
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SoundCombosViewModel : ComposeViewModel() {
    private val _items = MutableStateFlow<List<ComboSoundBite>>(emptyList())
    val items: StateFlow<List<ComboSoundBite>> = _items.asStateFlow()

    private val _selectedItem = MutableStateFlow<ComboSoundBite?>(null)
    val selectedItem: StateFlow<ComboSoundBite?> = _selectedItem.asStateFlow()

    init {
        refreshItems()
    }

    fun onItemSelected(item: ComboSoundBite?) {
        _selectedItem.value = item
    }

    fun onHelpClicked() {
        showInformation(getString("header_about_sound_combos"), getString("content_about_sound_combos"))
    }

    fun onRemoveClicked() {
        _selectedItem.value?.let {
            ConfigPersistence.removeSoundCombo(it)
            SoundBites.refreshCombos()
            refreshItems()
            _selectedItem.value = null
        }
    }

    fun onAddClicked() {
        openCreateSoundComboScreen {
            refreshItems()
        }
    }

    fun onEditClicked(item: ComboSoundBite) {
        openCreateSoundComboScreen(item) {
            refreshItems()
        }
    }

    private fun refreshItems() {
        _items.value = SoundBites.getComboSoundBites().sortedBy { it.name }
    }
}