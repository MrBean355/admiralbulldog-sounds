package com.github.mrbean355.admiralbulldog.common.update

import com.github.mrbean355.admiralbulldog.arch.AssetInfo
import com.github.mrbean355.admiralbulldog.arch.ComposeViewModel
import com.github.mrbean355.admiralbulldog.arch.repo.GitHubRepository
import com.github.mrbean355.admiralbulldog.common.AlertButton
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.common.showError
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream

class DownloadUpdateViewModel(
    private val assetInfo: AssetInfo,
    private val destination: String,
    private val onSuccess: () -> Unit,
    private val onCancel: () -> Unit
) : ComposeViewModel() {
    private val gitHubRepository = GitHubRepository()

    private val _header = MutableStateFlow(getString("msg_downloading"))
    val header: StateFlow<String> = _header.asStateFlow()

    private val _progress = MutableStateFlow<Float?>(null) // null for indeterminate
    val progress: StateFlow<Float?> = _progress.asStateFlow()

    init {
        download()
    }

    private fun download() {
        _header.value = getString("msg_downloading")
        _progress.value = null
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
            _header.value = getString("msg_downloading_with_size", "$formatted MB")

            val directory = File(destination)
            if (!directory.exists()) {
                directory.mkdirs()
            }
            try {
                val file = File(directory, assetInfo.name)
                body.byteStream().use {
                    it.streamToFile(file) { currentBytes ->
                        _progress.value = (currentBytes / totalBytes).toFloat()
                    }
                }
                onSuccess()
                requestWindowClose()
            } catch (e: FileNotFoundException) {
                showErrorMessage(getString("msg_update_failed_file_not_found"))
            }
        }
    }

    private fun showErrorMessage(message: String) {
        showError(getString("header_update_failed"), message, AlertButton.RETRY, AlertButton.CANCEL) { action ->
            if (action == AlertButton.RETRY) {
                download()
            } else {
                onCancel()
                requestWindowClose()
            }
        }
    }

    private fun Double.format(decimalPlaces: Int): String {
        return "%.${decimalPlaces}f".format(this)
    }

    private suspend fun InputStream.streamToFile(file: File, onProgress: (Long) -> Unit) = withContext(IO) {
        file.outputStream().use { output ->
            var bytesCopied = 0L
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            var bytes = read(buffer)
            while (bytes >= 0) {
                yield()
                output.write(buffer, 0, bytes)
                bytesCopied += bytes
                onProgress(bytesCopied)
                bytes = read(buffer)
            }
        }
    }
}
