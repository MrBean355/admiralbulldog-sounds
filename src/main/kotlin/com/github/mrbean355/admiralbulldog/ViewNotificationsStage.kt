package com.github.mrbean355.admiralbulldog

import com.github.mrbean355.admiralbulldog.persistence.Notifications
import com.github.mrbean355.admiralbulldog.ui.finalise
import javafx.geometry.Insets
import javafx.scene.control.Button
import javafx.scene.control.TextArea
import javafx.scene.layout.VBox
import javafx.stage.Stage

class ViewNotificationsStage : Stage() {
    private val textArea = TextArea()

    init {
        val root = VBox(PADDING_SMALL).apply {
            padding = Insets(PADDING_MEDIUM)
        }

        root.children += textArea.apply {
            isWrapText = true
            text = Notifications.getAll().joinToString(separator = "\n")
            selectPositionCaret(Int.MAX_VALUE)
            deselect()
        }
        root.children += Button(ACTION_CLEAR).apply {
            setOnAction { clearClicked() }
        }

        finalise(title = TITLE_NOTIFICATIONS, root = root)
    }

    private fun clearClicked() {
        Notifications.clear()
        textArea.text = ""
    }
}