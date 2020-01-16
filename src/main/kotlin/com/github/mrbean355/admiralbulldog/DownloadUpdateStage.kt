package com.github.mrbean355.admiralbulldog

import com.github.mrbean355.admiralbulldog.arch.AssetInfo
import com.github.mrbean355.admiralbulldog.ui.finalise
import com.github.mrbean355.admiralbulldog.ui.getString
import javafx.geometry.Insets
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ProgressBar
import javafx.scene.layout.VBox
import javafx.stage.Stage

class DownloadUpdateStage(
        assetInfo: AssetInfo,
        destination: String,
        onDownloadComplete: () -> Unit
) : Stage() {

    init {
        val viewModel = DownloadUpdateViewModel(assetInfo, destination, onDownloadComplete)
        val root = VBox(PADDING_SMALL).apply {
            padding = Insets(PADDING_MEDIUM)
        }
        root.children += Label().apply {
            textProperty().bind(viewModel.header)
        }
        root.children += ProgressBar().apply {
            prefWidthProperty().bind(root.widthProperty())
            progressProperty().bind(viewModel.progress)
        }
        root.children += Button(getString("btn_cancel")).apply {
            setOnAction {
                close()
            }
        }

        width = WINDOW_WIDTH
        finalise(TITLE_MAIN_WINDOW, root)
        setOnHiding {
            viewModel.onClose()
        }
        viewModel.isComplete.addListener { _, _, newValue ->
            if (newValue) {
                close()
            }
        }
        viewModel.init()
    }
}
