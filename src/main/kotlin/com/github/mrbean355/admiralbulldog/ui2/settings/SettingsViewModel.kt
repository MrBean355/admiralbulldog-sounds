package com.github.mrbean355.admiralbulldog.ui2.settings

import com.github.mrbean355.admiralbulldog.ui2.persistance.AppConfig
import tornadofx.ViewModel

class SettingsViewModel : ViewModel() {
    val volumeProperty = AppConfig.volumeProperty()
}