package com.github.mrbean355.admiralbulldog.arch

import javafx.application.Platform
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import tornadofx.ViewModel
import tornadofx.runLater
import kotlin.coroutines.CoroutineContext

val JavaFxDispatcher = object : CoroutineDispatcher() {
    override fun isDispatchNeeded(context: CoroutineContext): Boolean {
        return !Platform.isFxApplicationThread()
    }

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        Platform.runLater(block)
    }
}

abstract class AppViewModel : ViewModel() {
    protected val viewModelScope = CoroutineScope(JavaFxDispatcher + SupervisorJob())

    init {
        runLater { onReady() }
    }

    protected open fun onReady() {}

    open fun onUndock() {
        viewModelScope.cancel()
    }
}
