package com.github.mrbean355.admiralbulldog.ui

import com.github.mrbean355.admiralbulldog.DotaApplication
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import javafx.application.Platform
import javafx.application.Platform.runLater
import javafx.stage.Stage
import tornadofx.onChange
import java.awt.MenuItem
import java.awt.PopupMenu
import java.awt.SystemTray
import java.awt.TrayIcon
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

private var trayIcon: TrayIcon? = null

/** Prepare to show a [TrayIcon] when the app is minimized. */
fun prepareTrayIcon(stage: Stage) {
    if (!SystemTray.isSupported()) {
        return
    }

    trayIcon = buildTrayIcon(stage)
    refreshSystemTray()

    stage.iconifiedProperty().onChange { minimised ->
        if (minimised) onAppMinimised(stage) else onAppMaximised()
    }
    stage.setOnCloseRequest {
        it.consume()
        Platform.exit()
    }
}

fun refreshSystemTray() {
    Platform.setImplicitExit(!ConfigPersistence.isMinimizeToTray())
    if (ConfigPersistence.isAlwaysShowTrayIcon()) {
        if (trayIcon !in SystemTray.getSystemTray().trayIcons) {
            SystemTray.getSystemTray().add(trayIcon)
        }
    } else {
        SystemTray.getSystemTray().remove(trayIcon)
    }
}

private fun buildTrayIcon(stage: Stage): TrayIcon {
    val popup = PopupMenu()
    MenuItem(getString("menu_show")).apply {
        addActionListener { maximiseFromTray(stage) }
        popup.add(this)
    }
    MenuItem(getString("menu_close")).apply {
        addActionListener { exitFromTray() }
        popup.add(this)
    }
    return TrayIcon(getTrayImage(), getString("title_app"), popup).apply {
        isImageAutoSize = true
        addActionListener { maximiseFromTray(stage) }
    }
}

private fun onAppMinimised(stage: Stage) {
    if (ConfigPersistence.isMinimizeToTray()) {
        minimizeToTray(stage)
    }
    if (!ConfigPersistence.isAlwaysShowTrayIcon()) {
        SystemTray.getSystemTray().add(trayIcon)
    }
    if (!ConfigPersistence.getAndSetNotifiedAboutSystemTray()) {
        trayIcon?.displayMessage(getString("tray_caption"), getString("tray_message"), TrayIcon.MessageType.INFO)
    }
}

private fun onAppMaximised() {
    if (!ConfigPersistence.isAlwaysShowTrayIcon()) {
        SystemTray.getSystemTray().remove(trayIcon)
    }
}

private fun getTrayImage(): BufferedImage {
    return ImageIO.read(DotaApplication::class.java.classLoader.getResourceAsStream("bulldog.jpg"))
}

private fun maximiseFromTray(stage: Stage) {
    runLater {
        stage.isIconified = false
        stage.show()
    }
}

private fun minimizeToTray(stage: Stage) {
    runLater {
        stage.hide()
    }
}

private fun exitFromTray() {
    runLater {
        Platform.exit()
    }
}