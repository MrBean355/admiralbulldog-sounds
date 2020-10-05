package com.github.mrbean355.admiralbulldog.common.update

import com.github.mrbean355.admiralbulldog.arch.AppViewModel
import com.github.mrbean355.admiralbulldog.arch.AssetInfo
import com.github.mrbean355.admiralbulldog.arch.repo.GitHubRepository
import com.github.mrbean355.admiralbulldog.common.RETRY_BUTTON
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.common.showError
import javafx.beans.property.DoubleProperty
import javafx.beans.property.StringProperty
import javafx.scene.control.ButtonType
import javafx.scene.control.ProgressIndicator.INDETERMINATE_PROGRESS
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import tornadofx.FXEvent
import tornadofx.doubleProperty
import tornadofx.stringProperty
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream

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
        viewModelScope.launch {
            val resource = gitHubRepository.downloadAsset(assetInfo)
            val body = resource.body
            if (!resource.isSuccessful() || body == null) {
                showErrorMessage(getString("msg_update_failed_unknown"))
                return@launch
            }
            val totalBytes = body.contentLength().toDouble()
            val totalMegabytes = totalBytes / 1024.0 / 1024
            val formatted = totalMegabytes.format(decimalPlaces = 2)
            header.set(getString("msg_downloading_with_size", "$formatted MB"))

            val directory = File(destination)
            if (!directory.exists()) {
                directory.mkdirs()
            }
            try {
                val file = File(directory, assetInfo.name)
                body.byteStream().use {
                    it.streamToFile(file) { currentBytes ->
                        progress.set(currentBytes / totalBytes)
                    }
                }
                fire(CloseEvent(success = true))
            } catch (e: FileNotFoundException) {
                showErrorMessage(getString("msg_update_failed_file_not_found"))
            }
        }
    }

    private fun showErrorMessage(message: String) {
        showError(getString("header_update_failed"), message, RETRY_BUTTON, ButtonType.CANCEL) {
            if (it === RETRY_BUTTON) {
                download()
            } else {
                fire(CloseEvent(success = false))
            }
        }
    }

    private fun Double.format(decimalPlaces: Int): String {
        return "%.${decimalPlaces}f".format(this)
    }

    /**
     * Stream this [InputStream] into the given [file], calling [onProgress] with the current number of bytes written.
     */
    private suspend fun InputStream.streamToFile(file: File, onProgress: (Long) -> Unit) = withContext(IO) {
        file.outputStream().use { output ->
            var bytesCopied = 0L
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            var bytes = read(buffer)
            while (bytes >= 0) {
                yield()
                output.write(buffer, 0, bytes)
                bytesCopied += bytes
                withContext(Main) {
                    onProgress(bytesCopied)
                }
                bytes = read(buffer)
            }
        }
    }

    class CloseEvent(val success: Boolean) : FXEvent()

}
