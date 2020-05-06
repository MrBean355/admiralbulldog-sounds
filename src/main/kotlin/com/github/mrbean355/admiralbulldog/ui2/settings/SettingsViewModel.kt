package com.github.mrbean355.admiralbulldog.ui2.settings

import com.github.mrbean355.admiralbulldog.ui2.openUpdateModal
import com.github.mrbean355.admiralbulldog.ui2.persistance.AppConfig
import com.github.mrbean355.admiralbulldog.ui2.update.CheckForUpdateScreen
import com.github.mrbean355.admiralbulldog.ui2.update.UpdateFrequency
import tornadofx.ViewModel
import tornadofx.toObservable

class SettingsViewModel : ViewModel() {
    val volumeProperty = AppConfig.volumeProperty()
    val selectedUpdateFrequency = AppConfig.updateFrequencyProperty()
    val updateFrequencies = UpdateFrequency.values().asList().toObservable()

    fun onCheckForUpdateClicked() {
        find<CheckForUpdateScreen>().openUpdateModal()
    }
}