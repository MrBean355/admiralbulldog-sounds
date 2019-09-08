package com.github.mrbean355.admiralbulldog

import com.github.mrbean355.admiralbulldog.game.monitorGameStateUpdates
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import javafx.application.Application
import javafx.application.Application.launch
import javafx.application.Platform
import javafx.beans.binding.Bindings.createStringBinding
import javafx.beans.property.SimpleBooleanProperty
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.text.Font
import javafx.stage.Modality
import javafx.stage.Stage
import java.io.File
import java.io.PrintStream
import java.util.concurrent.Callable
import kotlin.system.exitProcess

fun main() {
    Thread.setDefaultUncaughtExceptionHandler { t, e ->
        val file = File("crash_log.txt")
        e.printStackTrace(PrintStream(file))
        file.appendText(t.toString())
    }
    launch(DotaApplication::class.java)
}

class DotaApplication : Application() {
    private val isLoaded = SimpleBooleanProperty(false)
    private val newVersionObservable = checkForNewVersion()

    override fun init() {
        ConfigPersistence.initialise()
    }

    override fun start(primaryStage: Stage) {
        val root = VBox(PADDING_SMALL).apply {
            padding = Insets(PADDING_MEDIUM)
            alignment = Pos.CENTER
        }

        root.children += Label().apply {
            font = Font(TEXT_SIZE_LARGE)
            textProperty().bind(createStringBinding(Callable {
                if (isLoaded.get()) MSG_CONNECTED else MSG_NOT_CONNECTED
            }, isLoaded))
        }
        root.children += ProgressBar().apply {
            prefWidthProperty().bind(root.widthProperty())
            visibleProperty().bind(isLoaded.not())
            managedProperty().bind(visibleProperty())
        }
        root.children += Label().apply {
            textProperty().bind(createStringBinding(Callable {
                if (isLoaded.get()) DESC_CONNECTED else DESC_NOT_CONNECTED
            }, isLoaded))
        }
        root.children += HBox(PADDING_SMALL).apply {
            alignment = Pos.CENTER
            children += Button(ACTION_CHANGE_SOUNDS).apply {
                setOnAction { changeSoundsClicked(primaryStage) }
            }
            children += Button(ACTION_DISCORD_BOT).apply {
                setOnAction { discordBotClicked(primaryStage) }
            }
        }
        root.children += Hyperlink(LINK_NEED_HELP).apply {
            setOnAction { needHelpClicked() }
        }
        root.children += Label(LABEL_APP_VERSION.format(APP_VERSION)).apply {
            font = Font(TEXT_SIZE_SMALL)
        }

        val newVersion = HBox().apply {
            alignment = Pos.CENTER
            children += Label(MSG_NEW_VERSION)
            children += Hyperlink(LINK_DOWNLOAD).apply {
                setOnAction { downloadClicked() }
            }
            visibleProperty().bind(newVersionObservable)
            managedProperty().bind(visibleProperty())
        }

        root.children += newVersion
        monitorGameStateUpdates { onNewGameState() }

        primaryStage.apply {
            scene = Scene(root)
            icons.add(bulldogIcon())
            title = TITLE_MAIN_WINDOW
            setOnCloseRequest { exitProcess(0) }
            show()
        }

        if (ConfigPersistence.getInvalidSounds().isNotEmpty()) {
            Alert(Alert.AlertType.WARNING, MSG_REMOVED_SOUNDS.format(ConfigPersistence.getInvalidSounds().joinToString(separator = "\n")))
                    .showAndWait()
        }
    }

    private fun onNewGameState() {
        if (!isLoaded.get()) {
            Platform.runLater { isLoaded.set(true) }
        }
    }

    private fun changeSoundsClicked(stage: Stage) {
        ToggleSoundBytesStage().apply {
            initModality(Modality.WINDOW_MODAL)
            initOwner(stage)
            show()
        }
    }

    private fun discordBotClicked(stage: Stage) {
        ConfigureDiscordBotStage().apply {
            initModality(Modality.WINDOW_MODAL)
            initOwner(stage)
            show()
        }
    }

    private fun needHelpClicked() {
        hostServices.showDocument(URL_NEED_HELP)
    }

    private fun downloadClicked() {
        hostServices.showDocument(URL_DOWNLOAD)
    }
}