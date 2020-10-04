package com.github.mrbean355.admiralbulldog.settings

import com.github.mrbean355.admiralbulldog.arch.AppViewModel
import com.github.mrbean355.admiralbulldog.common.*
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import com.github.mrbean355.admiralbulldog.sounds.sync.SyncSoundBitesScreen
import com.github.mrbean355.admiralbulldog.ui.refreshSystemTray
import javafx.beans.property.BooleanProperty
import javafx.beans.property.IntegerProperty
import javafx.beans.property.ObjectProperty
import kotlinx.coroutines.launch
import tornadofx.*
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
    val modEnabled: BooleanProperty = booleanProperty(ConfigPersistence.hasEnabledMods())
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
        soundsUpdateFrequency.onChange {
            it?.let { ConfigPersistence.setSoundsUpdateFrequency(it) }
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
        updateViewModel.checkForModUpdates(
                onError = { error(getString("header_update_check_failed"), getString("content_update_check_failed")) },
                onNoUpdate = { information(getString("header_mods_up_to_date"), getString("content_mods_up_to_date")) })
    }

    fun onProjectWebsiteClicked() {
        hostServices.showDocument(URL_PROJECT_WEBSITE)
    }
}
