package com.github.mrbean355.admiralbulldog.arch

import javafx.application.Platform
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

val JavaFxDispatcher = object : CoroutineDispatcher() {
    override fun isDispatchNeeded(context: CoroutineContext): Boolean {
        return !Platform.isFxApplicationThread()
    }

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        Platform.runLater(block)
    }
}

val SwingDispatcher = object : CoroutineDispatcher() {
    override fun isDispatchNeeded(context: CoroutineContext): Boolean {
        return !java.awt.EventQueue.isDispatchThread()
    }

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        java.awt.EventQueue.invokeLater(block)
    }
}

abstract class ComposeViewModel {
    protected val viewModelScope = CoroutineScope(SwingDispatcher + SupervisorJob())

    private val _requestWindowClose = MutableSharedFlow<Unit>()
    val requestWindowClose = _requestWindowClose.asSharedFlow()

    fun requestWindowClose() {
        viewModelScope.launch {
            _requestWindowClose.emit(Unit)
        }
    }

    open fun onCleared() {
        viewModelScope.cancel()
    }
}
