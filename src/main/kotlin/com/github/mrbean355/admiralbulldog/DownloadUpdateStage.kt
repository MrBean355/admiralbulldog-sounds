package com.github.mrbean355.admiralbulldog

import com.github.mrbean355.admiralbulldog.arch.AssetInfo
import com.github.mrbean355.admiralbulldog.ui.Alert
import com.github.mrbean355.admiralbulldog.ui.finalise
import com.github.mrbean355.admiralbulldog.ui.getString
import com.github.mrbean355.admiralbulldog.ui.toNullable
import javafx.geometry.Insets
import javafx.scene.control.Alert.AlertType
import javafx.scene.control.Button
import javafx.scene.control.ButtonBar
import javafx.scene.control.ButtonType
import javafx.scene.control.Label
import javafx.scene.control.ProgressBar
import javafx.scene.layout.VBox
import javafx.stage.Stage

class DownloadUpdateStage(
        assetInfo: AssetInfo,
        destination: String,
        onDownloadComplete: () -> Unit
) : Stage() {

    private val viewModel = DownloadUpdateViewModel(assetInfo, destination, onDownloadComplete)

    init {
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
        viewModel.error.addListener { _, _, newValue ->
            newValue?.let(this::showErrorDialog)
        }
        viewModel.init()
    }

    private fun showErrorDialog(message: String) {
        val retryButton = ButtonType(getString("btn_retry"), ButtonBar.ButtonData.OK_DONE)
        val choice = Alert(AlertType.ERROR, getString("header_update_failed"), message, arrayOf(ButtonType.CANCEL, retryButton))
                .showAndWait()

        if (choice.toNullable() === retryButton) {
            viewModel.onRetryClicked()
        } else {
            close()
        }
    }
}
