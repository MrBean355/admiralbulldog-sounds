package com.github.mrbean355.admiralbulldog

import com.github.mrbean355.admiralbulldog.assets.SoundFiles
import com.github.mrbean355.admiralbulldog.game.monitorGameStateUpdates
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import com.github.mrbean355.admiralbulldog.service.hostUrl
import com.github.mrbean355.admiralbulldog.service.logAnalyticsEvent
import com.github.mrbean355.admiralbulldog.service.whenLaterVersionAvailable
import com.github.mrbean355.admiralbulldog.ui.createGsiFile
import com.github.mrbean355.admiralbulldog.ui.finalise
import com.github.mrbean355.admiralbulldog.ui.prepareTrayIcon
import com.github.mrbean355.admiralbulldog.ui.showModal
import javafx.application.Application
import javafx.application.Application.launch
import javafx.application.Platform
import javafx.beans.binding.Bindings.createStringBinding
import javafx.beans.property.SimpleBooleanProperty
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Alert
import javafx.scene.control.Button
import javafx.scene.control.Hyperlink
import javafx.scene.control.Label
import javafx.scene.control.ProgressBar
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.text.Font
import javafx.stage.Stage
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.util.concurrent.Callable
import kotlin.system.exitProcess

/** Program argument to point to a custom host. Points to prod if omitted. */
private const val ARG_HOST_URL = "--host-url"

fun main(args: Array<String>) {
    setCustomHostUrl(args)
    Thread.setDefaultUncaughtExceptionHandler { t, e ->
        val file = File("crash_log.txt")
        val stringWriter = StringWriter()
        e.printStackTrace(PrintWriter(stringWriter))
        val stackTrace = stringWriter.toString()
        runCatching { logAnalyticsEvent("unhandled_exception", stackTrace) }
        file.writeText(stackTrace)
        file.appendText(t.toString())
    }
    launch(DotaApplication::class.java)
}

private fun setCustomHostUrl(args: Array<String>) {
    val hostUrlArg = args.firstOrNull { it.startsWith(ARG_HOST_URL) } ?: return
    hostUrl = hostUrlArg.substringAfterLast('=')
    if (hostUrl.isNotBlank()) {
        println("Using custom host URL: $hostUrl")
    }
}

class DotaApplication : Application() {
    private val isLoaded = SimpleBooleanProperty(false)

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
        root.children += HBox(PADDING_SMALL).apply {
            alignment = Pos.CENTER
            children += Hyperlink(LINK_INSTALL).apply {
                setOnAction { installClicked(primaryStage) }
                visibleProperty().bind(isLoaded.not())
                managedProperty().bind(visibleProperty())
            }
            children += Hyperlink(LINK_NEED_HELP).apply {
                setOnAction { needHelpClicked() }
            }
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
            visibleProperty().set(false)
            managedProperty().bind(visibleProperty())
        }

        whenLaterVersionAvailable {
            newVersion.isVisible = true
        }

        root.children += newVersion
        monitorGameStateUpdates { onNewGameState() }

        primaryStage.finalise(title = TITLE_MAIN_WINDOW, root = root, closeOnEscape = false, onCloseRequest = EventHandler {
            exitProcess(0)
        })

        logAnalyticsEvent(eventType = "app_start", eventData = APP_VERSION)
        if (SoundFiles.shouldSync()) {
            SyncSoundBytesStage().showModal(owner = primaryStage, wait = true)
        }
        resume(primaryStage)
    }

    private fun resume(primaryStage: Stage) {
        primaryStage.show()
        prepareTrayIcon(primaryStage)
        val invalidSounds = ConfigPersistence.getInvalidSounds()
        if (invalidSounds.isNotEmpty()) {
            Alert(Alert.AlertType.WARNING, MSG_REMOVED_SOUNDS.format(invalidSounds.joinToString(separator = "\n")))
                    .showAndWait()
            ConfigPersistence.clearInvalidSounds()
        }
    }

    private fun onNewGameState() {
        if (!isLoaded.get()) {
            Platform.runLater { isLoaded.set(true) }
        }
    }

    private fun changeSoundsClicked(stage: Stage) {
        logAnalyticsEvent(eventType = "button_click", eventData = "change_sounds")
        ToggleSoundEventsStage().showModal(owner = stage)
    }

    private fun discordBotClicked(stage: Stage) {
        logAnalyticsEvent(eventType = "button_click", eventData = "discord_bot_setup")
        DiscordBotStage(hostServices).showModal(owner = stage)
    }

    private fun installClicked(stage: Stage) {
        logAnalyticsEvent(eventType = "button_click", eventData = "install")
        createGsiFile(stage)
    }

    private fun needHelpClicked() {
        logAnalyticsEvent(eventType = "button_click", eventData = "need_help")
        hostServices.showDocument(URL_NEED_HELP)
    }

    private fun downloadClicked() {
        logAnalyticsEvent(eventType = "button_click", eventData = "download_latest_version")
        hostServices.showDocument(URL_DOWNLOAD)
    }
}