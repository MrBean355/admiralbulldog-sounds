package com.github.mrbean355.admiralbulldog.arch

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.SupervisorJob
import tornadofx.ViewModel
import tornadofx.runLater

abstract class AppViewModel : ViewModel() {
    protected val coroutineScope = CoroutineScope(IO + SupervisorJob())

    init {
        runLater { onReady() }
    }

    protected abstract fun onReady()

}
