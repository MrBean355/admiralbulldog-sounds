package com.github.mrbean355.admiralbulldog.ui2

import com.github.mrbean355.admiralbulldog.ui.getString
import com.github.mrbean355.admiralbulldog.ui2.home.HomeScreen
import com.github.mrbean355.admiralbulldog.ui2.settings.SettingsScreen
import com.github.mrbean355.admiralbulldog.ui2.sounds.SoundEventsScreen
import javafx.scene.control.TabPane.TabClosingPolicy.UNAVAILABLE
import tornadofx.View
import tornadofx.tab
import tornadofx.tabpane

class MainScreen : View(getString("app_title")) {
    override val root = tabpane {
        prefWidth = Window.WIDTH
        prefHeight = Window.HEIGHT
        primaryStage.isResizable = false
        tabClosingPolicy = UNAVAILABLE
        tab<HomeScreen>()
        tab<SoundEventsScreen>()
        tab(getString("tab_discord"))
        tab(getString("tab_mod"))
        tab<SettingsScreen>()
    }
}
