package com.github.mrbean355.admiralbulldog.settings

import com.github.mrbean355.admiralbulldog.arch.AppViewModel
import com.github.mrbean355.admiralbulldog.common.URL_PROJECT_WEBSITE
import com.github.mrbean355.admiralbulldog.common.confirmation
import com.github.mrbean355.admiralbulldog.common.error
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.common.information
import com.github.mrbean355.admiralbulldog.common.warning
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import com.github.mrbean355.admiralbulldog.sounds.sync.SyncSoundBitesScreen
import com.github.mrbean355.admiralbulldog.ui.refreshSystemTray
import javafx.beans.property.BooleanProperty
import javafx.beans.property.IntegerProperty
import javafx.beans.property.ObjectProperty
import javafx.scene.control.ButtonType
import kotlinx.coroutines.launch
import tornadofx.booleanProperty
import tornadofx.intProperty
import tornadofx.objectProperty
import tornadofx.onChange
import java.awt.SystemTray

class SettingsViewModel : AppViewModel() {
    private val updateViewModel by inject<UpdateViewModel>()

    val appVolume: IntegerProperty = intProperty(ConfigPersistence.getVolume())
    val traySupported: BooleanProperty = booleanProperty(SystemTray.isSupported())
    val minimizeToTray: BooleanProperty = booleanProperty(ConfigPersistence.isMinimizeToTray())
    val alwaysShowTrayIcon: BooleanProperty = booleanProperty(ConfigPersistence.isAlwaysShowTrayIcon())

    val updateFrequencies: List<UpdateFrequency> = UpdateFrequency.values().toList()
    val appUpdateFrequency: ObjectProperty<UpdateFrequency> = objectProperty(ConfigPersistence.getAppUpdateFrequency())
    val soundsUpdateFrequency: ObjectProperty<UpdateFrequency> = objectProperty(ConfigPersistence.getSoundsUpdateFrequency())
    val modEnabled: BooleanProperty = booleanProperty(ConfigPersistence.isModEnabled())
    val modUpdateFrequency: ObjectProperty<UpdateFrequency> = objectProperty(ConfigPersistence.getModUpdateFrequency())

    init {
        appVolume.onChange { ConfigPersistence.setVolume(it) }
        minimizeToTray.onChange {
            ConfigPersistence.setMinimizeToTray(it)
            refreshSystemTray()
        }
        alwaysShowTrayIcon.onChange {
            ConfigPersistence.setAlwaysShowTrayIcon(it)
            refreshSystemTray()
        }
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
        find<SyncSoundBitesScreen>().openModal(escapeClosesWindow = false, resizable = false)
    }

    fun onCheckForModUpdateClicked() {
        confirmation(getString("header_close_dota"), getString("content_close_dota")) {
            if (it === ButtonType.OK) {
                coroutineScope.launch {
                    updateViewModel.checkForModUpdate(
                            onError = { error(getString("header_update_check_failed"), getString("content_update_check_failed")) },
                            onNoUpdate = { information(getString("header_up_to_date"), getString("content_mod_up_to_date")) }
                    )
                }
            }
        }
    }

    fun onProjectWebsiteClicked() {
        hostServices.showDocument(URL_PROJECT_WEBSITE)
    }
}
