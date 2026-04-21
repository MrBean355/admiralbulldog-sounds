package com.github.mrbean355.admiralbulldog.ui

import androidx.compose.runtime.Composable
import com.github.mrbean355.admiralbulldog.arch.ComposeViewModel

fun <VM : ComposeViewModel> openComposeScreen(
    viewModelClass: Class<VM>,
    title: String,
    viewModelFactory: () -> VM,
    onCloseRequest: (() -> Unit)? = null,
    escapeClosesWindow: Boolean = true,
    content: @Composable (VM) -> Unit
) {
    WindowManager.openScreen(
        viewModelClass = viewModelClass,
        title = title,
        viewModelFactory = viewModelFactory,
        onCloseRequest = onCloseRequest,
        escapeClosesWindow = escapeClosesWindow,
        content = content
    )
}

inline fun <reified VM : ComposeViewModel> openComposeScreen(
    title: String,
    noinline viewModelFactory: () -> VM,
    noinline onCloseRequest: (() -> Unit)? = null,
    escapeClosesWindow: Boolean = true,
    crossinline content: @Composable (VM) -> Unit
) {
    openComposeScreen(VM::class.java, title, viewModelFactory, onCloseRequest, escapeClosesWindow) { content(it) }
}
