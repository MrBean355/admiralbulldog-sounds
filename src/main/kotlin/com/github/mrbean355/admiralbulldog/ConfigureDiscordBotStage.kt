package com.github.mrbean355.admiralbulldog

import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.VBox
import javafx.stage.Stage

class ConfigureDiscordBotStage : Stage() {
    private val selectedProperty = SimpleBooleanProperty(ConfigPersistence.isUsingDiscordBot())
    private val textProperty = SimpleStringProperty(ConfigPersistence.getDiscordToken())

    init {
        val root = VBox(PADDING_SMALL)
        root.padding = Insets(PADDING_MEDIUM)

        root.children += CheckBox(LABEL_ENABLE_DISCORD_BOT).apply {
            selectedProperty().bindBidirectional(selectedProperty)
        }
        root.children += TextField().apply {
            disableProperty().bind(selectedProperty.not())
            textProperty().bindBidirectional(textProperty)
        }
        root.children += Button(ACTION_SAVE).apply {
            setOnAction { saveClicked() }
        }

        title = TITLE_DISCORD_BOT
        scene = Scene(root)
        icons.add(bulldogIcon())
        width = WINDOW_WIDTH
        addEventFilter(KeyEvent.KEY_PRESSED) {
            if (it.code == KeyCode.ESCAPE) {
                close()
            }
        }
    }

    private fun saveClicked() {
        if (selectedProperty.get() && textProperty.get().isNotBlank()) {
            ConfigPersistence.setDiscordToken(textProperty.get())
        } else {
            ConfigPersistence.setDiscordToken(null)
        }
        close()
    }
}