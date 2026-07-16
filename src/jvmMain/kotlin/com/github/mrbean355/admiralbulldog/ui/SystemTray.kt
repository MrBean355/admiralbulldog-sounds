package com.github.mrbean355.admiralbulldog.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Tray
import androidx.compose.ui.window.rememberTrayState
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.home.MainViewModel
import com.github.mrbean355.admiralbulldog_sounds.generated.resources.Res
import com.github.mrbean355.admiralbulldog_sounds.generated.resources.bulldog
import org.jetbrains.compose.resources.painterResource

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
    val icon = painterResource(Res.drawable.bulldog)

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