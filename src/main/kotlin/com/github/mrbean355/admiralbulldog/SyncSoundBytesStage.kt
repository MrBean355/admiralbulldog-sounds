package com.github.mrbean355.admiralbulldog

import com.github.mrbean355.admiralbulldog.assets.SoundFiles
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import javafx.application.Platform
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.ProgressBar
import javafx.scene.control.TextArea
import javafx.scene.layout.VBox
import javafx.stage.Stage

class SyncSoundBytesStage : Stage() {
    private val progress = SimpleDoubleProperty()
    private val log = SimpleStringProperty(MSG_SYNC_WELCOME)

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

        SoundFiles.synchronise(action = {
            Platform.runLater {
                log.value = "${log.value}\n$it"
            }
        }, success = {
            ConfigPersistence.markLastSync()
            setOnCloseRequest { /* Default behaviour */ }
        })

        title = TITLE_SYNC_SOUND_BYTES
        scene = Scene(root)
        icons.add(bulldogIcon())
        width = WINDOW_WIDTH
        setOnCloseRequest { it.consume() }
    }
}
