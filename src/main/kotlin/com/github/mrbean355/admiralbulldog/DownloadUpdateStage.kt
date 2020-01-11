package com.github.mrbean355.admiralbulldog

import com.github.mrbean355.admiralbulldog.service.ReleaseInfo
import com.github.mrbean355.admiralbulldog.service.UpdateDownloader
import com.github.mrbean355.admiralbulldog.ui.Alert
import com.github.mrbean355.admiralbulldog.ui.finalise
import com.github.mrbean355.admiralbulldog.ui.format
import com.github.mrbean355.admiralbulldog.ui.toNullable
import javafx.geometry.Insets
import javafx.scene.control.Alert
import javafx.scene.control.Button
import javafx.scene.control.ButtonType
import javafx.scene.control.Label
import javafx.scene.control.ProgressBar
import javafx.scene.layout.VBox
import javafx.stage.Stage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import org.slf4j.LoggerFactory
import kotlin.system.exitProcess

class DownloadUpdateStage(private val releaseInfo: ReleaseInfo) : Stage() {
    private val logger = LoggerFactory.getLogger(DownloadUpdateStage::class.java)
    private val coroutineScope = CoroutineScope(Dispatchers.Default) + Job()
    private val header = Label("Downloading update...")
    private val progressBar = ProgressBar(ProgressBar.INDETERMINATE_PROGRESS)

    init {
        val root = VBox(PADDING_SMALL).apply {
            padding = Insets(PADDING_MEDIUM)
        }
        root.children += header
        root.children += progressBar.apply {
            prefWidthProperty().bind(root.widthProperty())
        }
        root.children += Button("Cancel").apply {
            setOnAction {
                close()
            }
        }

        width = WINDOW_WIDTH
        finalise(TITLE_MAIN_WINDOW, root)
        setOnHiding {
            logger.info("Window closed, cancelling download")
            coroutineScope.cancel()
        }
        onViewCreated()
    }

    private fun onViewCreated() {
        val downloader = UpdateDownloader()
        coroutineScope.launch {
            downloader.download(releaseInfo)
        }
        downloader.totalBytes.addListener { _, _, newValue ->
            val megabytes = newValue.toLong() / 1024.0 / 1024
            val formatted = megabytes.format(decimalPlaces = 2)
            header.text = "Downloading update (${formatted} MB)..."
        }
        downloader.progress.addListener { _, _, newValue ->
            progressBar.progress = newValue as Double
        }
        downloader.onComplete = { filePath ->
            Alert(type = Alert.AlertType.INFORMATION,
                    header = HEADER_UPDATER,
                    content = """
                        Please run the new version in future:
                        $filePath
                        The app will now close.
                    """.trimIndent(),
                    buttons = arrayOf(ButtonType.FINISH),
                    owner = this
            ).showAndWait().toNullable()
            exitProcess(0)
        }
    }
}
