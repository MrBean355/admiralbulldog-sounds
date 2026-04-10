package com.github.mrbean355.admiralbulldog.settings

import com.github.mrbean355.admiralbulldog.arch.ComposeViewModel
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.common.showError
import com.github.mrbean355.admiralbulldog.common.showInformation
import com.github.mrbean355.admiralbulldog.common.showWarning
import com.github.mrbean355.admiralbulldog.feedback.openFeedbackScreen
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import com.github.mrbean355.admiralbulldog.sounds.sync.SyncSoundBitesScreen
import com.github.mrbean355.admiralbulldog.styles.reloadAppStyles
import com.github.mrbean355.admiralbulldog.ui.refreshSystemTray
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import tornadofx.FX
import java.awt.SystemTray

class SettingsViewModel : ComposeViewModel() {
    private val updateViewModel: UpdateViewModel by lazy {
        FX.find(UpdateViewModel::class.java)
    }

    private val _appVolume = MutableStateFlow(ConfigPersistence.getVolume())
    val appVolume = _appVolume.asStateFlow()

    private val _darkMode = MutableStateFlow(ConfigPersistence.isDarkMode())
    val darkMode = _darkMode.asStateFlow()

    val traySupported = SystemTray.isSupported()

    private val _minimizeToTray = MutableStateFlow(ConfigPersistence.isMinimizeToTray())
    val minimizeToTray = _minimizeToTray.asStateFlow()

    private val _alwaysShowTrayIcon = MutableStateFlow(ConfigPersistence.isAlwaysShowTrayIcon())
    val alwaysShowTrayIcon = _alwaysShowTrayIcon.asStateFlow()

    val updateFrequencies = UpdateFrequency.values().toList()

    private val _appUpdateFrequency = MutableStateFlow(ConfigPersistence.getAppUpdateFrequency())
    val appUpdateFrequency = _appUpdateFrequency.asStateFlow()

    private val _soundsUpdateFrequency = MutableStateFlow(ConfigPersistence.getSoundsUpdateFrequency())
    val soundsUpdateFrequency = _soundsUpdateFrequency.asStateFlow()

    val modEnabled = ConfigPersistence.hasEnabledMods()

    private val _modUpdateFrequency = MutableStateFlow(ConfigPersistence.getModUpdateFrequency())
    val modUpdateFrequency = _modUpdateFrequency.asStateFlow()

    override fun onCleared() {
        updateViewModel.onUndock()
        super.onCleared()
    }

    fun setAppVolume(volume: Int) {
        _appVolume.value = volume
        ConfigPersistence.setVolume(volume)
    }

    fun setDarkMode(enabled: Boolean) {
        _darkMode.value = enabled
        ConfigPersistence.setIsDarkMode(enabled)
        reloadAppStyles()
    }

    fun setMinimizeToTray(minimize: Boolean) {
        _minimizeToTray.value = minimize
        ConfigPersistence.setMinimizeToTray(minimize)
        refreshSystemTray()
    }

    fun setAlwaysShowTrayIcon(show: Boolean) {
        _alwaysShowTrayIcon.value = show
        ConfigPersistence.setAlwaysShowTrayIcon(show)
        refreshSystemTray()
    }

    fun setAppUpdateFrequency(frequency: UpdateFrequency) {
        _appUpdateFrequency.value = frequency
        ConfigPersistence.setAppUpdateFrequency(frequency)
    }

    fun setSoundsUpdateFrequency(frequency: UpdateFrequency) {
        _soundsUpdateFrequency.value = frequency
        ConfigPersistence.setSoundsUpdateFrequency(frequency)
    }

    fun setModUpdateFrequency(frequency: UpdateFrequency) {
        _modUpdateFrequency.value = frequency
        ConfigPersistence.setModUpdateFrequency(frequency)
        if (frequency > UpdateFrequency.DAILY) {
            showWarning(getString("header_mod_update_frequency"), getString("content_mod_update_frequency"))
        }
    }

    fun onCheckForAppUpdateClicked() {
        updateViewModel.checkForAppUpdate(
            onError = { showError(getString("header_update_check_failed"), getString("content_update_check_failed")) },
            onNoUpdate = { showInformation(getString("header_up_to_date"), getString("content_app_up_to_date")) }
        )
    }

    fun onUpdateSoundsClicked() {
        tornadofx.find<SyncSoundBitesScreen>().openModal(escapeClosesWindow = false, resizable = false)
    }

    fun onCheckForModUpdateClicked() {
        updateViewModel.checkForModUpdates(
            onError = { showError(getString("header_update_check_failed"), getString("content_update_check_failed")) },
            onNoUpdate = { showInformation(getString("header_mods_up_to_date"), getString("content_mods_up_to_date")) })
    }

    fun onMoreInformationClicked() {
        openMoreInformationScreen()
    }

    fun onSendFeedbackClicked() {
        openFeedbackScreen()
    }
}