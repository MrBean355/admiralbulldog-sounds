package com.github.mrbean355.admiralbulldog

import com.github.mrbean355.admiralbulldog.assets.SoundBytes
import com.github.mrbean355.admiralbulldog.game.monitorGameStateUpdates
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import com.github.mrbean355.admiralbulldog.service.ReleaseInfo
import com.github.mrbean355.admiralbulldog.service.UpdateChecker
import com.github.mrbean355.admiralbulldog.service.hostUrl
import com.github.mrbean355.admiralbulldog.service.logAnalyticsEvent
import com.github.mrbean355.admiralbulldog.ui.Alert
import com.github.mrbean355.admiralbulldog.ui.DotaPath
import com.github.mrbean355.admiralbulldog.ui.Installer
import com.github.mrbean355.admiralbulldog.ui.finalise
import com.github.mrbean355.admiralbulldog.ui.prepareTrayIcon
import com.github.mrbean355.admiralbulldog.ui.removeVersionPrefix
import com.github.mrbean355.admiralbulldog.ui.showModal
import com.github.mrbean355.admiralbulldog.ui.toNullable
import com.vdurmont.semver4j.Semver
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
import javafx.scene.control.ButtonBar
import javafx.scene.control.ButtonType
import javafx.scene.control.Hyperlink
import javafx.scene.control.Label
import javafx.scene.control.ProgressBar
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.text.Font
import javafx.stage.Stage
import javafx.stage.Window
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.Callable
import kotlin.system.exitProcess

/** Program argument to point to a custom host. Points to prod if omitted. */
private const val ARG_HOST_URL = "--host-url"

fun main(args: Array<String>) {
    setCustomHostUrl(args)
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
        Thread.setDefaultUncaughtExceptionHandler(UncaughtExceptionHandlerImpl(hostServices))
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
            children += Hyperlink(LINK_DISCORD_COMMUNITY).apply {
                setOnAction { discordCommunityClicked() }
            }
            children += Hyperlink(LINK_PROJECT_WEBSITE).apply {
                setOnAction { projectWebsiteClicked() }
            }
        }
        root.children += Label(LABEL_APP_VERSION.format(APP_VERSION)).apply {
            font = Font(TEXT_SIZE_SMALL)
        }

        primaryStage.finalise(title = TITLE_MAIN_WINDOW, root = root, closeOnEscape = false, onCloseRequest = EventHandler {
            exitProcess(0)
        })
        onViewCreated(primaryStage)
    }

    private fun onViewCreated(stage: Stage) {
        if (SoundBytes.shouldSync()) {
            SyncSoundBytesStage().showModal(owner = stage, wait = true)
        }

        val result = runCatching {
            DotaPath.loadPath(ownerWindow = stage)
        }.onFailure {
            Alert(type = Alert.AlertType.ERROR,
                    header = HEADER_INSTALLER,
                    content = MSG_SPECIFY_VALID_DOTA_DIR,
                    buttons = arrayOf(ButtonType.CLOSE),
                    owner = stage
            ).showAndWait()
            exitProcess(-1)
        }

        prepareTrayIcon(stage)
        Installer.installIfNecessary(result.getOrDefault(""))

        val invalidSounds = ConfigPersistence.getInvalidSounds()
        if (invalidSounds.isNotEmpty()) {
            Alert(type = Alert.AlertType.WARNING,
                    header = HEADER_REMOVED_SOUNDS,
                    content = MSG_REMOVED_SOUNDS.format(invalidSounds.joinToString(separator = "\n")),
                    buttons = arrayOf(ButtonType.OK),
                    owner = stage
            ).showAndWait()
            ConfigPersistence.clearInvalidSounds()
        }

        val updateChecker = UpdateChecker()
        GlobalScope.launch {
            val releaseInfo = updateChecker.getLatestReleaseInfo() ?: return@launch
            val currentVersion = Semver(APP_VERSION)
            val latestVersion = Semver(releaseInfo.tagName.removeVersionPrefix())
            if (latestVersion > currentVersion) {
                withContext(Dispatchers.Main) {
                    onNewerVersionAvailable(stage, releaseInfo)
                }
            }
        }

        monitorGameStateUpdates { onNewGameState() }
        logAnalyticsEvent(eventType = "app_start", eventData = APP_VERSION)
        stage.show()
    }

    private fun onNewerVersionAvailable(ownerWindow: Window, releaseInfo: ReleaseInfo) {
        val jarAssetInfo = releaseInfo.getJarAssetInfo() ?: return
        val infoButton = ButtonType("Info", ButtonBar.ButtonData.HELP_2)
        val downloadButton = ButtonType("Download", ButtonBar.ButtonData.NEXT_FORWARD)
        val action = Alert(
                type = Alert.AlertType.INFORMATION,
                header = HEADER_UPDATE_AVAILABLE,
                content = """
                    Version: ${releaseInfo.name}
                    Published at: ${releaseInfo.publishedAt}
                """.trimIndent(),
                buttons = arrayOf(infoButton, downloadButton, ButtonType.CANCEL),
                owner = ownerWindow
        ).showAndWait().toNullable()

        if (action === infoButton) {
            hostServices.showDocument(releaseInfo.htmlUrl)
            onNewerVersionAvailable(ownerWindow, releaseInfo)
        } else if (action === downloadButton) {
            DownloadUpdateStage(jarAssetInfo, destination = ".")
                    .setOnComplete {
                        onUpdateDownloaded(ownerWindow, it)
                    }.show()
        }
    }

    private fun onUpdateDownloaded(ownerWindow: Window, filePath: String) {
        Alert(type = Alert.AlertType.INFORMATION,
                header = HEADER_UPDATER,
                content = """
                        Please run the new version in future:
                        $filePath
                        The app will now close.
                    """.trimIndent(),
                buttons = arrayOf(ButtonType.FINISH),
                owner = ownerWindow
        ).showAndWait()
        exitProcess(0)
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

    private fun discordCommunityClicked() {
        logAnalyticsEvent(eventType = "button_click", eventData = "discord_community")
        hostServices.showDocument(URL_DISCORD_INVITE)
    }

    private fun projectWebsiteClicked() {
        logAnalyticsEvent(eventType = "button_click", eventData = "project_website")
        hostServices.showDocument(URL_PROJECT_WEBSITE)
    }
}