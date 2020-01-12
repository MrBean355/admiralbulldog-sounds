package com.github.mrbean355.admiralbulldog

import com.github.mrbean355.admiralbulldog.service.AssetInfo
import com.github.mrbean355.admiralbulldog.service.UpdateDownloader
import com.github.mrbean355.admiralbulldog.ui.finalise
import com.github.mrbean355.admiralbulldog.ui.format
import javafx.geometry.Insets
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ProgressBar
import javafx.scene.layout.VBox
import javafx.stage.Stage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory

class DownloadUpdateStage(private val assetInfo: AssetInfo, private val destination: String) : Stage() {
    private val logger = LoggerFactory.getLogger(DownloadUpdateStage::class.java)
    private val coroutineScope = CoroutineScope(Dispatchers.Default) + Job()
    private val header = Label("Downloading update...")
    private val progressBar = ProgressBar(ProgressBar.INDETERMINATE_PROGRESS)
    private val downloader = UpdateDownloader()

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

    fun setOnComplete(onComplete: (fileUrl: String) -> Unit): DownloadUpdateStage {
        downloader.onComplete = onComplete
        return this
    }

    private fun onViewCreated() {
        coroutineScope.launch {
            downloader.download(assetInfo, destination)
            withContext(Main) {
                close()
            }
        }
        downloader.totalBytes.addListener { _, _, newValue ->
            val megabytes = newValue.toLong() / 1024.0 / 1024
            val formatted = megabytes.format(decimalPlaces = 2)
            header.text = "Downloading update (${formatted} MB)..."
        }
        downloader.progress.addListener { _, _, newValue ->
            progressBar.progress = newValue as Double
        }
    }
}
