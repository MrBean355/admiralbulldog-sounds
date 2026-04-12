package com.github.mrbean355.admiralbulldog.sounds.manager

import com.github.mrbean355.admiralbulldog.arch.ComposeViewModel
import com.github.mrbean355.admiralbulldog.common.Volume
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.common.showInformation
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class VolumeManagerViewModel : ComposeViewModel() {
    private val _items = MutableStateFlow<List<Volume>>(emptyList())
    val items: StateFlow<List<Volume>> = _items.asStateFlow()

    private val _selectedItem = MutableStateFlow<Volume?>(null)
    val selectedItem: StateFlow<Volume?> = _selectedItem.asStateFlow()

    init {
        refreshItems()
    }

    fun onItemSelected(item: Volume?) {
        _selectedItem.value = item
    }

    fun onHelpClicked() {
        showInformation(getString("header_about_volume_manager"), getString("content_about_volume_manager"))
    }

    fun onRemoveVolumeClicked() {
        _selectedItem.value?.let {
            ConfigPersistence.removeSoundBiteVolume(it.name)
            refreshItems()
            _selectedItem.value = null
        }
    }

    fun onAddVolumeClicked() {
        openChooseVolumeScreen {
            refreshItems()
        }
    }

    fun onEditVolumeClicked(item: Volume) {
        openChooseVolumeScreen(item.name) {
            refreshItems()
        }
    }

    private fun refreshItems() {
        _items.value = ConfigPersistence.getSoundBiteVolumes()
            .map { Volume(it.key, it.value) }
            .sortedBy { it.name }
    }
}