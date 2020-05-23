package com.github.mrbean355.admiralbulldog.settings

import com.github.mrbean355.admiralbulldog.arch.AppViewModel
import com.github.mrbean355.admiralbulldog.common.error
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.common.information
import com.github.mrbean355.admiralbulldog.common.warning
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import com.github.mrbean355.admiralbulldog.sounds.SyncSoundBitesScreen
import javafx.beans.property.ObjectProperty
import kotlinx.coroutines.launch
import tornadofx.objectProperty
import tornadofx.onChange

class SettingsViewModel : AppViewModel() {
    private val updateViewModel by inject<UpdateViewModel>()

    val updateFrequencies: List<UpdateFrequency> = UpdateFrequency.values().toList()
    val appUpdateFrequency: ObjectProperty<UpdateFrequency> = objectProperty(ConfigPersistence.getAppUpdateFrequency())
    val soundsUpdateFrequency: ObjectProperty<UpdateFrequency> = objectProperty(ConfigPersistence.getSoundsUpdateFrequency())
    val modUpdateFrequency: ObjectProperty<UpdateFrequency> = objectProperty(ConfigPersistence.getModUpdateFrequency())

    init {
        updateViewModel
        appUpdateFrequency.onChange {
            it?.let { ConfigPersistence.setAppUpdateFrequency(it) }
        }
        modUpdateFrequency.onChange {
            it?.let {
                ConfigPersistence.setModUpdateFrequency(it)
                if (it > UpdateFrequency.DAILY) {
                    warning("Dota mod updates", "You should check regularly (on startup or daily) for mod updates, or you will see strange text in-game.")
                }
            }
        }
    }

    override fun onUndock() {
        updateViewModel.onUndock()
        super.onUndock()
    }

    fun onCheckForAppUpdateClicked() {
        coroutineScope.launch {
            updateViewModel.checkForAppUpdate(
                    onError = { error(getString("header_update_check_failed"), getString("content_update_check_failed")) },
                    onNoUpdate = { information(getString("header_up_to_date"), getString("content_app_up_to_date")) }
            )
        }
    }

    fun onUpdateSoundsClicked() {
        find<SyncSoundBitesScreen>().openModal(escapeClosesWindow = false, block = true, resizable = false)
    }

    fun onCheckForModUpdateClicked() {
        coroutineScope.launch {
            updateViewModel.checkForModUpdate(
                    onError = { error(getString("header_update_check_failed"), getString("content_update_check_failed")) },
                    onNoUpdate = { information(getString("header_up_to_date"), getString("content_mod_up_to_date")) }
            )
        }
    }
}
