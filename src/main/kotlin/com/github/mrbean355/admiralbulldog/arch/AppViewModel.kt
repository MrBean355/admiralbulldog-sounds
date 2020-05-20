package com.github.mrbean355.admiralbulldog.arch

import tornadofx.ViewModel
import tornadofx.runLater

abstract class AppViewModel : ViewModel() {

    init {
        runLater { onReady() }
    }

    abstract fun onReady()

}
