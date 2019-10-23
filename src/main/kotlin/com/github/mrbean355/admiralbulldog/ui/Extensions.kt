package com.github.mrbean355.admiralbulldog.ui

import com.github.mrbean355.admiralbulldog.bulldogIcon
import javafx.event.EventHandler
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.stage.Modality.WINDOW_MODAL
import javafx.stage.Stage
import javafx.stage.Window
import javafx.stage.WindowEvent

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