package com.github.mrbean355.admiralbulldog.arch

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

abstract class ComposeViewModel {
    protected val viewModelScope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob())

    open fun onCleared() {
        viewModelScope.cancel()
    }
}
