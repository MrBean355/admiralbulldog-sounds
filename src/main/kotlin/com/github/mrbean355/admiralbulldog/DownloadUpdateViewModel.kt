package com.github.mrbean355.admiralbulldog

import com.github.mrbean355.admiralbulldog.arch.AssetInfo
import com.github.mrbean355.admiralbulldog.arch.GitHubRepository
import com.github.mrbean355.admiralbulldog.ui.format
import com.github.mrbean355.admiralbulldog.ui.streamToFile
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleStringProperty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class DownloadUpdateViewModel(
        private val assetInfo: AssetInfo,
        private val destination: String,
        private val onDownloadComplete: () -> Unit
) {
    private val coroutineScope = CoroutineScope(Default + Job())
    private val gitHubRepository = GitHubRepository()

    val header = SimpleStringProperty("Downloading update...")
    val progress = SimpleDoubleProperty()
    val isComplete = SimpleBooleanProperty(false)

    fun init() {
        coroutineScope.launch {
            val resource = gitHubRepository.downloadAsset(assetInfo)
            val body = resource.body()
            if (!resource.isSuccessful || body == null) {
                return@launch
            }
            val totalBytes = body.contentLength().toDouble()
            val totalMegabytes = totalBytes / 1024.0 / 1024
            val formatted = totalMegabytes.format(decimalPlaces = 2)
            header.set("Downloading update ($formatted MB)...")
            val directory = File(destination)
            if (!directory.exists()) {
                directory.mkdirs()
            }
            val file = File(directory, assetInfo.name)
            body.byteStream().use {
                it.streamToFile(file) { currentBytes ->
                    withContext(Main) {
                        progress.set(currentBytes / totalBytes)
                    }
                }
            }
            withContext(Main) {
                isComplete.set(true)
                onDownloadComplete()
            }
        }
    }

    fun onClose() {
        coroutineScope.cancel()
    }
}
