package com.github.mrbean355.admiralbulldog.ui2.update

import com.github.mrbean355.admiralbulldog.WINDOW_WIDTH
import com.github.mrbean355.admiralbulldog.ui.getString
import com.github.mrbean355.admiralbulldog.ui.toNullable
import com.github.mrbean355.admiralbulldog.ui2.CustomButtonType
import com.github.mrbean355.admiralbulldog.ui2.Spacing
import com.github.mrbean355.admiralbulldog.ui2.alert
import com.vdurmont.semver4j.Semver
import javafx.geometry.Pos.CENTER
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import tornadofx.Fragment
import tornadofx.Scope
import tornadofx.fitToParentWidth
import tornadofx.label
import tornadofx.paddingAll
import tornadofx.progressbar
import tornadofx.vbox

class CheckForUpdateScreen : Fragment(getString("title_update")) {
    private val viewModel by inject<CheckForUpdateViewModel>(Scope())

    override val root = vbox(spacing = Spacing.SMALL, alignment = CENTER) {
        paddingAll = Spacing.MEDIUM
        label(viewModel.description)
        progressbar(viewModel.progress) {
            fitToParentWidth()
        }
        prefWidth = WINDOW_WIDTH
    }

    init {
        subscribe<CheckForUpdateViewModel.ErrorEvent> { showErrorAlert() }
        subscribe<CheckForUpdateViewModel.NewVersionEvent> { showNewVersionAvailableAlert(it.version) }
        subscribe<CheckForUpdateViewModel.UpToDateEvent> { showUpToDateAlert() }
        subscribe<CheckForUpdateViewModel.DownloadCompleteEvent> { showDownloadCompleteAlert(it.fileName) }
        viewModel.initialise()
    }

    private fun showErrorAlert() {
        val action = alert(Alert.AlertType.ERROR, getString("header_error"), getString("content_app_update_check_failed"), arrayOf(CustomButtonType.RETRY, ButtonType.CANCEL))
                .showAndWait()
                .toNullable()

        if (action == CustomButtonType.RETRY) {
            viewModel.onRetryClicked()
        } else {
            close()
        }
    }

    private fun showNewVersionAvailableAlert(version: Semver) {
        val action = alert(Alert.AlertType.INFORMATION, getString("header_new_app_version_available"), getString("content_new_app_version_available", version), arrayOf(CustomButtonType.DOWNLOAD, ButtonType.CANCEL))
                .showAndWait()
                .toNullable()

        if (action == CustomButtonType.DOWNLOAD) {
            viewModel.onDownloadClicked()
        } else {
            close()
        }
    }

    private fun showUpToDateAlert() {
        alert(Alert.AlertType.INFORMATION, getString("header_app_up_to_date"), getString("content_app_up_to_date"))
                .showAndWait()

        close()
    }

    private fun showDownloadCompleteAlert(fileName: String) {
        alert(Alert.AlertType.INFORMATION, getString("header_download_complete"), getString("content_download_complete", fileName))
                .showAndWait()

        close()
    }
}