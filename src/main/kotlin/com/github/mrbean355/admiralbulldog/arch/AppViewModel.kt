package com.github.mrbean355.admiralbulldog.arch

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import tornadofx.*

abstract class AppViewModel : ViewModel() {
    @Deprecated("Use viewModelScope", replaceWith = ReplaceWith("viewModelScope"))
    protected val coroutineScope = CoroutineScope(IO + SupervisorJob())
    protected val viewModelScope = CoroutineScope(Main.immediate + SupervisorJob())

    init {
        runLater { onReady() }
    }

    protected open fun onReady() {}

    open fun onUndock() {
        coroutineScope.cancel()
        viewModelScope.cancel()
    }
}
