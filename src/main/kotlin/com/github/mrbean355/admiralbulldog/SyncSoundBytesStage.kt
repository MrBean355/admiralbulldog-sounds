package com.github.mrbean355.admiralbulldog

import com.github.mrbean355.admiralbulldog.assets.SoundBytes
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import com.github.mrbean355.admiralbulldog.ui.finalise
import javafx.application.Platform
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleStringProperty
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.scene.control.Button
import javafx.scene.control.ProgressBar
import javafx.scene.control.TextArea
import javafx.scene.layout.VBox
import javafx.stage.Stage

class SyncSoundBytesStage : Stage() {
    private val progress = SimpleDoubleProperty()
    private val log = SimpleStringProperty(MSG_SYNC_WELCOME)
    private val complete = SimpleBooleanProperty(false)

    init {
        val root = VBox(PADDING_MEDIUM).apply {
            padding = Insets(PADDING_MEDIUM)
        }
        root.children += ProgressBar().apply {
            prefWidthProperty().bind(root.widthProperty())
            progressProperty().bind(this@SyncSoundBytesStage.progress)
        }
        root.children += TextArea().apply {
            isEditable = false
            textProperty().bind(log)
            log.addListener { _, _, _ ->
                // Using setScrollTop() doesn't work reliably
                selectPositionCaret(Int.MAX_VALUE)
                deselect()
            }
        }
        root.children += Button(ACTION_DONE).apply {
            disableProperty().bind(complete.not())
            setOnAction { close() }
        }

        SoundBytes.synchronise(action = {
            Platform.runLater {
                log.value = "${log.value}\n$it"
            }
        }, complete = { successful ->
            if (successful) {
                ConfigPersistence.markLastSync()
            }
            setOnCloseRequest { /* Default behaviour */ }
            complete.set(true)
        })
        finalise(title = TITLE_SYNC_SOUND_BYTES, root = root, onCloseRequest = EventHandler {
            it.consume()
        })
        width = WINDOW_WIDTH_LARGE
    }
}
