package com.github.mrbean355.admiralbulldog.ui

import com.github.mrbean355.admiralbulldog.ACTION_EXIT
import com.github.mrbean355.admiralbulldog.ACTION_SHOW
import com.github.mrbean355.admiralbulldog.DotaApplication
import com.github.mrbean355.admiralbulldog.TITLE_MAIN_WINDOW
import com.github.mrbean355.admiralbulldog.TRAY_CAPTION
import com.github.mrbean355.admiralbulldog.TRAY_MESSAGE
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import com.github.mrbean355.admiralbulldog.service.logAnalyticsEvent
import javafx.application.Platform
import javafx.stage.Stage
import java.awt.Image
import java.awt.MenuItem
import java.awt.PopupMenu
import java.awt.SystemTray
import java.awt.TrayIcon
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import kotlin.system.exitProcess

/** Prepare to show a [TrayIcon] when the app is minimized. */
fun prepareTrayIcon(stage: Stage) {
    if (!SystemTray.isSupported()) {
        val osName = System.getProperty("os.name")
        val osVersion = System.getProperty("os.version")
        val osArch = System.getProperty("os.arch")
        logAnalyticsEvent(eventType = "tray_unsupported", eventData = "os.name=$osName,os.version=$osVersion,os.arch=$osArch")
        return
    }
    Platform.setImplicitExit(false)

    val popup = PopupMenu()
    MenuItem(ACTION_SHOW).apply {
        addActionListener { showFromTray(stage) }
        popup.add(this)
    }
    MenuItem(ACTION_EXIT).apply {
        addActionListener { exitFromTray() }
        popup.add(this)
    }
    val trayIcon = TrayIcon(getTrayImage(), TITLE_MAIN_WINDOW, popup).apply {
        addActionListener { showFromTray(stage) }
    }

    stage.iconifiedProperty().addListener { _, _, newValue ->
        if (newValue) {
            minimizeToTray(stage)
            SystemTray.getSystemTray().add(trayIcon)
            if (!ConfigPersistence.getAndSetNotifiedAboutSystemTray()) {
                trayIcon.displayMessage(TRAY_CAPTION, TRAY_MESSAGE, TrayIcon.MessageType.INFO)
            }
        } else {
            SystemTray.getSystemTray().remove(trayIcon)
        }
    }
}

private fun getTrayImage(): Image {
    val size = SystemTray.getSystemTray().trayIconSize
    val image = ImageIO.read(DotaApplication::class.java.classLoader.getResourceAsStream("bulldog.jpg"))
    return image.getScaledInstance(size.width, size.height, BufferedImage.TYPE_INT_ARGB)
}

private fun showFromTray(stage: Stage) {
    Platform.runLater {
        stage.isIconified = false
        stage.show()
    }
}

private fun minimizeToTray(stage: Stage) {
    Platform.runLater {
        stage.hide()
    }
}

private fun exitFromTray() {
    Platform.runLater {
        exitProcess(0)
    }
}