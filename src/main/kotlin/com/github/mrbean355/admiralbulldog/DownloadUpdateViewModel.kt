package com.github.mrbean355.admiralbulldog

import com.github.mrbean355.admiralbulldog.arch.AppViewModel
import com.github.mrbean355.admiralbulldog.arch.AssetInfo
import com.github.mrbean355.admiralbulldog.arch.GitHubRepository
import com.github.mrbean355.admiralbulldog.ui.format
import com.github.mrbean355.admiralbulldog.ui.getString
import com.github.mrbean355.admiralbulldog.ui.streamToFile
import javafx.beans.property.DoubleProperty
import javafx.beans.property.StringProperty
import javafx.scene.control.ButtonType
import javafx.scene.control.ProgressIndicator.INDETERMINATE_PROGRESS
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tornadofx.FXEvent
import tornadofx.doubleProperty
import tornadofx.stringProperty
import java.io.File
import java.io.FileNotFoundException

class DownloadUpdateViewModel : AppViewModel() {
    private val gitHubRepository = GitHubRepository()
    private val assetInfo by param<AssetInfo>()
    private val destination by param<String>()

    val header: StringProperty = stringProperty()
    val progress: DoubleProperty = doubleProperty()

    override fun onReady() {
        download()
    }

    private fun download() {
        header.set(getString("msg_downloading"))
        progress.set(INDETERMINATE_PROGRESS)
        coroutineScope.launch {
            val resource = gitHubRepository.downloadAsset(assetInfo)
            val body = resource.body
            if (!resource.isSuccessful() || body == null) {
                withContext(Main) {
                    showErrorMessage(getString("msg_update_failed_unknown"))
                }
                return@launch
            }
            val totalBytes = body.contentLength().toDouble()
            val totalMegabytes = totalBytes / 1024.0 / 1024
            val formatted = totalMegabytes.format(decimalPlaces = 2)
            withContext(Main) {
                header.set(getString("msg_downloading_with_size", "$formatted MB"))
            }
            val directory = File(destination)
            if (!directory.exists()) {
                directory.mkdirs()
            }
            try {
                val file = File(directory, assetInfo.name)
                body.byteStream().use {
                    it.streamToFile(file) { currentBytes ->
                        withContext(Main) {
                            progress.set(currentBytes / totalBytes)
                        }
                    }
                }
                withContext(Main) {
                    fire(CloseEvent(success = true))
                }
            } catch (e: FileNotFoundException) {
                withContext(Main) {
                    showErrorMessage(getString("msg_update_failed_file_not_found"))
                }
            }
        }
    }

    private fun showErrorMessage(message: String) {
        error(getString("header_update_failed"), message, RETRY_BUTTON, ButtonType.CANCEL) {
            if (it === RETRY_BUTTON) {
                download()
            } else {
                fire(CloseEvent(success = false))
            }
        }
    }

    class CloseEvent(val success: Boolean) : FXEvent()

}
