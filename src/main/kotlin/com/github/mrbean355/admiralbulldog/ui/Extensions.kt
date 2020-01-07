package com.github.mrbean355.admiralbulldog.ui

import com.github.mrbean355.admiralbulldog.HEADER_INSTALLER
import com.github.mrbean355.admiralbulldog.TITLE_MAIN_WINDOW
import com.github.mrbean355.admiralbulldog.bulldogIcon
import javafx.event.EventHandler
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.stage.Modality.WINDOW_MODAL
import javafx.stage.Stage
import javafx.stage.Window
import javafx.stage.WindowEvent
import java.io.File

fun Stage.showModal(owner: Window, wait: Boolean = false) {
    initModality(WINDOW_MODAL)
    initOwner(owner)
    if (wait) {
        showAndWait()
    } else {
        show()
    }
}

fun Stage.finalise(title: String, root: Parent, closeOnEscape: Boolean = true, onCloseRequest: EventHandler<WindowEvent>? = null) {
    this.title = title
    scene = Scene(root)
    icons.add(bulldogIcon())
    if (closeOnEscape) {
        addEventFilter(KeyEvent.KEY_PRESSED) {
            if (it.code == KeyCode.ESCAPE) {
                close()
            }
        }
    }
    if (onCloseRequest != null) {
        setOnCloseRequest(onCloseRequest)
    }
}

fun Alert.showInstallerAndWait(): ButtonType? {
    title = TITLE_MAIN_WINDOW
    headerText = HEADER_INSTALLER
    return showAndWait().orElse(null)
}

fun String.replaceFileSeparators(): String {
    return replace("/", File.separator)
}
