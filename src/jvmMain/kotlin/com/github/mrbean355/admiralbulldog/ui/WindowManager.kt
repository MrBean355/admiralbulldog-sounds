package com.github.mrbean355.admiralbulldog.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.github.mrbean355.admiralbulldog.arch.ComposeViewModel

object WindowManager {
    val screens = mutableStateListOf<Screen<*>>()
    val dialogs = mutableStateListOf<DialogEntry>()

    fun <VM : ComposeViewModel> openScreen(
        viewModelClass: Class<VM>,
        title: String,
        viewModelFactory: () -> VM,
        onCloseRequest: (() -> Unit)? = null,
        escapeClosesWindow: Boolean = true,
        content: @Composable (VM) -> Unit
    ) {
        val existing = screens.find { it.viewModelClass == viewModelClass }
        if (existing != null) {
            existing.isVisibleState.value = true
            return
        }

        screens.add(
            Screen(
                viewModelClass = viewModelClass,
                title = title,
                viewModel = viewModelFactory(),
                onCloseRequest = onCloseRequest,
                escapeClosesWindow = escapeClosesWindow,
                content = content
            )
        )
    }

    fun closeScreen(screen: Screen<*>) {
        screen.viewModel.onCleared()
        screen.onCloseRequest?.invoke()
        screens.remove(screen)
    }

    fun openDialog(dialog: DialogEntry) {
        dialogs.add(dialog)
    }

    fun closeDialog(dialog: DialogEntry) {
        dialogs.remove(dialog)
    }
}

data class Screen<VM : ComposeViewModel>(
    val viewModelClass: Class<VM>,
    val title: String,
    val viewModel: VM,
    val onCloseRequest: (() -> Unit)?,
    val escapeClosesWindow: Boolean,
    val content: @Composable (VM) -> Unit
) {
    val isVisibleState = androidx.compose.runtime.mutableStateOf(true)

    @Composable
    fun render() {
        content(viewModel)
    }
}

data class DialogEntry(
    val title: String,
    val content: @Composable (DialogEntry) -> Unit
)
