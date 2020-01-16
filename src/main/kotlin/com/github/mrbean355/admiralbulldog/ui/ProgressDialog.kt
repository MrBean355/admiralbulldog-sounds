package com.github.mrbean355.admiralbulldog.ui

import com.github.mrbean355.admiralbulldog.PADDING_MEDIUM
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.control.ProgressIndicator
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.stage.Stage
import javafx.stage.StageStyle

/** Displays an indeterminate progress bar with label. Cannot be closed by the user. */
class ProgressDialog : Stage() {

    init {
        val root = VBox(PADDING_MEDIUM).apply {
            padding = Insets(PADDING_MEDIUM, 32.0, PADDING_MEDIUM, 32.0)
        }
        root.children += HBox().apply {
            alignment = Pos.CENTER
            children += ProgressIndicator()
        }
        root.children += Label("Please wait").apply {
            BorderPane.setMargin(this, Insets(PADDING_MEDIUM, 0.0, 0.0, 0.0))
        }

        initStyle(StageStyle.UTILITY)
        finalise("Loading", root, closeOnEscape = false)
        isResizable = false
        setOnCloseRequest {
            it.consume()
        }
    }
}