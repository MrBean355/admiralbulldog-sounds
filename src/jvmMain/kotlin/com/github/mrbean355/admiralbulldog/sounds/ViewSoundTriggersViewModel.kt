package com.github.mrbean355.admiralbulldog.sounds

import com.github.mrbean355.admiralbulldog.arch.ComposeViewModel
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.common.showInformation
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import com.github.mrbean355.admiralbulldog.sounds.manager.openSoundManagerScreen
import com.github.mrbean355.admiralbulldog.triggers.SOUND_TRIGGER_TYPES
import com.github.mrbean355.admiralbulldog.triggers.SoundTriggerType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ViewSoundTriggersViewModel : ComposeViewModel() {
    private val _triggerStates = MutableStateFlow<Map<SoundTriggerType, TriggerState>>(emptyMap())
    val triggerStates: StateFlow<Map<SoundTriggerType, TriggerState>> = _triggerStates.asStateFlow()

    init {
        refresh()
    }

    fun onConfigureClicked(type: SoundTriggerType) {
        openConfigureSoundTriggerScreen(type) {
            refresh()
        }
    }


    fun onManageSoundsClicked() {
        openSoundManagerScreen()
    }

    fun onHelpClicked() {
        showInformation(getString("header_about_sound_triggers"), getString("content_about_sound_triggers"))
    }

    private fun refresh() {
        _triggerStates.value = SOUND_TRIGGER_TYPES.associateWith { type ->
            TriggerState(
                text = textFor(type),
                isActive = isActiveFor(type)
            )
        }
    }

    private fun textFor(type: SoundTriggerType): String = when {
        !ConfigPersistence.isSoundTriggerEnabled(type) -> getString("label_trigger_disabled", type.friendlyName)
        ConfigPersistence.getSoundsForType(type).isEmpty() -> getString("label_trigger_no_sounds", type.friendlyName)
        else -> type.friendlyName
    }

    private fun isActiveFor(type: SoundTriggerType): Boolean {
        return ConfigPersistence.isSoundTriggerEnabled(type) && ConfigPersistence.getSoundsForType(type).isNotEmpty()
    }

    data class TriggerState(
        val text: String,
        val isActive: Boolean
    )
}
