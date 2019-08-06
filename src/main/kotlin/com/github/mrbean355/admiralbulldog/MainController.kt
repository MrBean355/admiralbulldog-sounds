package com.github.mrbean355.admiralbulldog

import com.github.mrbean355.admiralbulldog.game.monitorGameStateUpdates
import javafx.application.HostServices
import javafx.application.Platform
import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.control.ProgressBar
import javafx.scene.layout.Pane
import java.util.concurrent.atomic.AtomicBoolean

private const val GSI_PORT = 12345
private const val NEED_HELP_URL = "https://github.com/MrBean355/dota2-integration"
private const val DOWNLOAD_URL = "https://github.com/MrBean355/dota2-integration/releases"

class MainController {
    var hostServices: HostServices? = null
    private val firstUpdate = AtomicBoolean(true)
    @FXML
    private lateinit var root: Pane
    @FXML
    private lateinit var title: Label
    @FXML
    private lateinit var progressBar: ProgressBar
    @FXML
    private lateinit var description: Label
    @FXML
    private lateinit var newVersion: Pane

    fun initialize() {
        progressBar.prefWidthProperty().bind(root.widthProperty())
        progressBar.managedProperty().bind(progressBar.visibleProperty())
        newVersion.managedProperty().bind(newVersion.visibleProperty())
        monitorGameStateUpdates(GSI_PORT) { onNewGameState() }
        checkForNewVersion { Platform.runLater { newVersion.isVisible = true } }
    }

    fun needHelpClicked() {
        hostServices?.showDocument(NEED_HELP_URL)
    }

    fun downloadClicked() {
        hostServices?.showDocument(DOWNLOAD_URL)
    }

    fun configureClicked() {
        ToggleSoundBytesStage().show()
    }

    private fun onNewGameState() {
        if (firstUpdate.getAndSet(false)) {
            Platform.runLater {
                progressBar.isVisible = false
                title.text = "Connected to Dota 2!"
                description.text = "Ready to play sounds during your matches!"
                description.scene.window.sizeToScene()
            }
        }
    }
}