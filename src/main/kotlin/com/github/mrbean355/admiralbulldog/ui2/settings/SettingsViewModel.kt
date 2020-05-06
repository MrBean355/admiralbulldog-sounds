package com.github.mrbean355.admiralbulldog.ui2.settings

import com.github.mrbean355.admiralbulldog.ui2.openUpdateModal
import com.github.mrbean355.admiralbulldog.ui2.persistance.AppConfig
import com.github.mrbean355.admiralbulldog.ui2.update.CheckForUpdateScreen
import tornadofx.ViewModel

class SettingsViewModel : ViewModel() {
    val volumeProperty = AppConfig.volumeProperty()

    fun onCheckForUpdateClicked() {
        find<CheckForUpdateScreen>().openUpdateModal()
    }
}