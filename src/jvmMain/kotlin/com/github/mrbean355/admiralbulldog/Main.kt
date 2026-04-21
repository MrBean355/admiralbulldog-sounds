package com.github.mrbean355.admiralbulldog

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Alignment
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberDialogState
import androidx.compose.ui.window.rememberWindowState
import com.github.mrbean355.admiralbulldog.arch.repo.hostUrl
import com.github.mrbean355.admiralbulldog.exception.UncaughtExceptionHandlerImpl
import com.github.mrbean355.admiralbulldog.home.openMainScreen
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import com.github.mrbean355.admiralbulldog.ui.AppTray
import com.github.mrbean355.admiralbulldog.ui.WindowManager
import com.github.mrbean355.admiralbulldog.ui.theme.BulldogTheme

/** Program argument to point to a custom host. Points to prod if omitted. */
private const val ARG_HOST_URL = "--host-url"

fun main(args: Array<String>) {
    // Required for javafx.scene.media.MediaPlayer to work without TornadoFX.
    try {
        javafx.application.Platform.startup {}
    } catch (t: Throwable) {
        // Already started or failed to start, proceed anyway.
    }

    if (checkJavaVersion() && checkOperatingSystem()) {
        setCustomHostUrl(args)
        initialise()
        application {
            openMainScreen()
            AppTray()
            WindowManager.screens.forEach { screen ->
                Window(
                    onCloseRequest = { WindowManager.closeScreen(screen) },
                    state = rememberWindowState(
                        position = WindowPosition.Aligned(Alignment.Center),
                        size = DpSize.Unspecified
                    ),
                    title = screen.title,
                    visible = screen.isVisibleState.value,
                    resizable = false,
                    onPreviewKeyEvent = {
                        if (screen.escapeClosesWindow && it.key == Key.Escape && it.type == KeyEventType.KeyDown) {
                            WindowManager.closeScreen(screen)
                            true
                        } else {
                            false
                        }
                    }
                ) {
                    BulldogTheme {
                        Surface(color = MaterialTheme.colorScheme.background) {
                            screen.render()
                        }
                    }
                }
            }
            WindowManager.dialogs.forEach { dialog ->
                DialogWindow(
                    onCloseRequest = { WindowManager.closeDialog(dialog) },
                    state = rememberDialogState(
                        position = WindowPosition.Aligned(Alignment.Center),
                        size = DpSize.Unspecified
                    ),
                    title = dialog.title,
                    resizable = false
                ) {
                    BulldogTheme {
                        dialog.content(dialog)
                    }
                }
            }
        }
    }
}

private fun initialise() {
    ConfigPersistence.initialise()
    Thread.setDefaultUncaughtExceptionHandler(UncaughtExceptionHandlerImpl())
}

private fun setCustomHostUrl(args: Array<String>) {
    val hostUrlArg = args.firstOrNull { it.startsWith(ARG_HOST_URL) } ?: return
    hostUrl = hostUrlArg.substringAfterLast('=')
    if (hostUrl.isNotBlank()) {
        println("Using custom host URL: $hostUrl")
    }
}
