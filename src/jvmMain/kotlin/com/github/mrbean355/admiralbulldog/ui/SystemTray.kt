package com.github.mrbean355.admiralbulldog.ui

import com.github.mrbean355.admiralbulldog.DotaApplication
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import javafx.application.Platform
import java.awt.Frame
import java.awt.MenuItem
import java.awt.PopupMenu
import java.awt.SystemTray
import java.awt.TrayIcon
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import javax.swing.JFrame

private var trayIcon: TrayIcon? = null

/** Prepare to show a [TrayIcon] when the app is minimized. */
fun prepareTrayIcon(frame: JFrame) {
    if (!SystemTray.isSupported()) {
        return
    }

    trayIcon = buildTrayIcon(frame)
    refreshSystemTray()

    frame.addWindowListener(object : WindowAdapter() {
        override fun windowIconified(e: WindowEvent?) {
            onAppMinimised(frame)
        }

        override fun windowDeiconified(e: WindowEvent?) {
            onAppMaximised()
        }

        override fun windowClosing(e: WindowEvent?) {
            Platform.exit()
        }
    })
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

private fun buildTrayIcon(frame: JFrame): TrayIcon {
    val popup = PopupMenu()
    MenuItem(getString("menu_show")).apply {
        addActionListener { maximiseFromTray(frame) }
        popup.add(this)
    }
    MenuItem(getString("menu_close")).apply {
        addActionListener { exitFromTray() }
        popup.add(this)
    }
    return TrayIcon(getTrayImage(), getString("title_app"), popup).apply {
        isImageAutoSize = true
        addActionListener { maximiseFromTray(frame) }
    }
}

private fun onAppMinimised(frame: JFrame) {
    if (ConfigPersistence.isMinimizeToTray()) {
        minimizeToTray(frame)
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

private fun maximiseFromTray(frame: JFrame) {
    SwingUtilities.invokeLater {
        frame.extendedState = Frame.NORMAL
        frame.isVisible = true
        frame.toFront()
    }
}

private fun minimizeToTray(frame: JFrame) {
    SwingUtilities.invokeLater {
        frame.isVisible = false
    }
}

private fun exitFromTray() {
    Platform.exit()
}

// Helper for maximiseFromTray and minimizeToTray if SwingUtilities is needed
private object SwingUtilities {
    fun invokeLater(block: () -> Unit) {
        java.awt.EventQueue.invokeLater(block)
    }
}