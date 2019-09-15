package com.github.mrbean355.admiralbulldog

import com.github.mrbean355.admiralbulldog.assets.SoundFile
import com.github.mrbean355.admiralbulldog.discord.playSoundOnDiscord
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import javafx.application.HostServices
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.Alert
import javafx.scene.control.Button
import javafx.scene.control.ButtonType
import javafx.scene.control.CheckBox
import javafx.scene.control.Hyperlink
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.stage.Stage

class ConfigureDiscordBotStage(private val hostServices: HostServices) : Stage() {
    private val selectedProperty = SimpleBooleanProperty(ConfigPersistence.isUsingDiscordBot())
    private val textProperty = SimpleStringProperty(ConfigPersistence.getDiscordToken())

    init {
        val root = VBox(PADDING_SMALL)
        root.padding = Insets(PADDING_MEDIUM)

        root.children += CheckBox(LABEL_ENABLE_DISCORD_BOT).apply {
            selectedProperty().bindBidirectional(selectedProperty)
        }
        root.children += TextField().apply {
            promptText = PROMPT_DISCORD_MAGIC_NUMBER
            disableProperty().bind(selectedProperty.not())
            textProperty().bindBidirectional(textProperty)
        }
        root.children += Hyperlink(LABEL_DISCORD_BOT_HELP).apply {
            setOnAction {
                val buttonType = ButtonType(ACTION_MORE_INFO)
                val result = Alert(Alert.AlertType.INFORMATION, MSG_DISCORD_BOT_MORE_INFO, buttonType)
                        .showAndWait()

                if (result.isPresent && result.get() == buttonType) {
                    hostServices.showDocument(URL_DISCORD_BOT_HELP)
                }
            }
        }
        root.children += HBox(PADDING_SMALL).apply {
            children += Button(ACTION_SAVE).apply {
                setOnAction { saveClicked() }
            }
            children += Button(ACTION_TEST).apply {
                disableProperty().bind(selectedProperty.not())
                setOnAction {
                    playSoundOnDiscord(SoundFile.ROONS, token = textProperty.get())
                }
            }
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
        ConfigPersistence.setDiscordToken(selectedProperty.get(), textProperty.get())
        close()
    }
}