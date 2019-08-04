package com.github.mrbean355.dota2.integration

import com.github.mrbean355.dota2.integration.assets.SoundFileRegistry
import javafx.application.Application
import javafx.application.Application.launch
import javafx.application.Platform
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Hyperlink
import javafx.scene.control.Label
import javafx.scene.control.ProgressBar
import javafx.scene.image.Image
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.text.Font
import javafx.stage.Stage
import kotlin.system.exitProcess

private const val GSI_PORT = 12345
private const val ICON_RESOURCE_NAME = "bulldog.jpg"
private const val NEED_HELP_URL = "https://github.com/MrBean355/dota2-integration"
private const val DOWNLOAD_URL = "https://github.com/MrBean355/dota2-integration/releases"

private const val MIN_WIDTH = 300.0
private const val LARGE_FONT_SIZE = 18.0
private const val SMALL_PADDING = 8.0
private const val MEDIUM_PADDING = 16.0

fun main() {
    launch(DotaApplication::class.java)
}

class DotaApplication : Application() {
    private lateinit var primaryStage: Stage
    private lateinit var root: VBox
    private lateinit var title: Label
    private lateinit var progressBar: ProgressBar
    private lateinit var description: Label
    private lateinit var needHelp: Hyperlink

    override fun init() {
        SoundFileRegistry.initialise()
        val gameStateMonitor = GameStateMonitor()
        GameStateIntegrationServer(GSI_PORT) {
            Platform.runLater { onGameStateUpdate(it) }
            gameStateMonitor.onUpdate(it)
        }.start()
    }

    override fun start(primaryStage: Stage) {
        root = VBox(SMALL_PADDING)
        root.alignment = Pos.CENTER
        root.minWidth = MIN_WIDTH
        root.padding = Insets(MEDIUM_PADDING, MEDIUM_PADDING, MEDIUM_PADDING, MEDIUM_PADDING)

        title = Label("Waiting for first update from Dota 2...").apply {
            font = Font(LARGE_FONT_SIZE)
            root.children += this
        }
        progressBar = ProgressBar().apply {
            prefWidthProperty().bind(root.widthProperty())
            root.children += this
        }
        description = Label("You need to be in a match. Try entering Hero Demo mode.").apply {
            root.children += this
        }
        needHelp = Hyperlink("Need help?").apply {
            setOnAction { hostServices.showDocument(NEED_HELP_URL) }
            root.children += this
        }
        val newVersionContainer = HBox(SMALL_PADDING).apply {
            alignment = Pos.CENTER
        }
        Label("New version available!").apply {
            newVersionContainer.children += this
        }
        Hyperlink("Download").apply {
            setOnAction { hostServices.showDocument(DOWNLOAD_URL) }
            newVersionContainer.children += this
        }

        checkForNewVersion {
            Platform.runLater { root.children += newVersionContainer }
        }

        primaryStage.scene = Scene(root)
        primaryStage.icons.add(Image(javaClass.classLoader.getResourceAsStream(ICON_RESOURCE_NAME)))
        primaryStage.title = "Dota 2 Integration"
        primaryStage.isResizable = false
        primaryStage.setOnCloseRequest { exitProcess(0) }
        primaryStage.show()
        this.primaryStage = primaryStage
    }

    private fun onGameStateUpdate(gameState: GameState) {
        root.children.remove(progressBar)
        root.children.remove(needHelp)

        title.text = "Connected to Dota 2!"
        description.text = """
            Ready to play sounds during your matches!
            
            Player: ${gameState.player?.name.orNone()}
            Hero: ${gameState.hero?.name.friendlyHeroName()}
            Match: ${gameState.map?.matchid.orNone()}
        """.trimIndent()

        primaryStage.sizeToScene()
    }

    private fun String?.orNone(): String {
        return if (this == null || this.isBlank()) "(none)" else this
    }

    /** Converts a string like "npc_dota_hero_bounty_hunter" to "Bounty Hunter". */
    private fun String?.friendlyHeroName(): String {
        return orNone().replace("npc_dota_hero_", "")
                .replace('_', ' ')
                .split(' ')
                .joinToString(separator = " ") { it.capitalize() }
    }
}