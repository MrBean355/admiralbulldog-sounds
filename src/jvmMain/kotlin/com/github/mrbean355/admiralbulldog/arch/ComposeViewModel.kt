package com.github.mrbean355.admiralbulldog.arch

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch


abstract class ComposeViewModel {
    protected val viewModelScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

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
