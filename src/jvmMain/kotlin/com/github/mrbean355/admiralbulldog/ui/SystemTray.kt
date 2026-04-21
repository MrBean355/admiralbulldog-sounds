package com.github.mrbean355.admiralbulldog.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.window.Tray
import androidx.compose.ui.window.rememberTrayState
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.home.MainViewModel

@Composable
fun androidx.compose.ui.window.ApplicationScope.AppTray() {
    // Optimization: only render the Tray if it's set to 'always show' or if we have hidden windows.
    // Commented out because it might be causing the application block to exit on startup.
    /*
    if (!ConfigPersistence.isAlwaysShowTrayIcon()) {
        val anyHidden = remember(WindowManager.screens.size) {
            WindowManager.screens.any { !it.isVisibleState.value }
        }
        if (!anyHidden) return
    }
    */

    val trayState = rememberTrayState()
    val icon = remember {
        val resource = Thread.currentThread().contextClassLoader.getResourceAsStream("bulldog.jpg")
        BitmapPainter(loadImageBitmap(resource!!))
    }

    Tray(
        state = trayState,
        icon = icon,
        tooltip = getString("title_app")
    ) {
        Item(getString("menu_show"), onClick = {
            WindowManager.screens.find { it.viewModelClass == MainViewModel::class.java }?.let {
                it.isVisibleState.value = true
            }
        })
        Item(getString("menu_close"), onClick = {
            kotlin.system.exitProcess(0)
        })
    }
}