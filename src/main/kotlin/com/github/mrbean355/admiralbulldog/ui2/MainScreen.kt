package com.github.mrbean355.admiralbulldog.ui2

import com.github.mrbean355.admiralbulldog.ui.DotaPath
import com.github.mrbean355.admiralbulldog.ui.getString
import com.github.mrbean355.admiralbulldog.ui2.discord.DiscordBotScreen
import com.github.mrbean355.admiralbulldog.ui2.home.HomeScreen
import com.github.mrbean355.admiralbulldog.ui2.installation.InstallationWizard
import com.github.mrbean355.admiralbulldog.ui2.persistance.AppConfig
import com.github.mrbean355.admiralbulldog.ui2.settings.SettingsScreen
import com.github.mrbean355.admiralbulldog.ui2.sounds.SoundTriggersScreen
import javafx.scene.control.Alert.AlertType.ERROR
import javafx.scene.control.TabPane.TabClosingPolicy.UNAVAILABLE
import tornadofx.Scope
import tornadofx.View
import tornadofx.find
import tornadofx.runLater
import tornadofx.tab
import tornadofx.tabpane
import kotlin.system.exitProcess

class MainScreen : View(getString("app_title")) {

    override val root = tabpane {
        prefWidth = Window.WIDTH
        prefHeight = Window.HEIGHT
        primaryStage.isResizable = false
        tabClosingPolicy = UNAVAILABLE
        tab<HomeScreen>()
        tab<SoundTriggersScreen>()
        tab<DiscordBotScreen>()
        tab(getString("tab_mod"))
        tab<SettingsScreen>()
    }

    init {
        runLater {
            var hasValidDotaPath = DotaPath.getDotaRootDirectory(AppConfig.dotaPathProperty().get()) != null
            if (hasValidDotaPath) {
                return@runLater
            }

            find<InstallationWizard>(scope = Scope()).openModal(resizable = false, block = true)
            hasValidDotaPath = DotaPath.getDotaRootDirectory(AppConfig.dotaPathProperty().get()) != null

            if (hasValidDotaPath) {
                return@runLater
            }
            alert(
                    type = ERROR,
                    header = getString("header_dota_path_required"),
                    content = getString("content_dota_path_required")
            ).showAndWait()
            exitProcess(0)
        }
    }
}
