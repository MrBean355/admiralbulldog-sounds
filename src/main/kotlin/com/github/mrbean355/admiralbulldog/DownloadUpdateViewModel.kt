package com.github.mrbean355.admiralbulldog

import com.github.mrbean355.admiralbulldog.arch.GitHubRepository
import com.github.mrbean355.admiralbulldog.service.AssetInfo
import com.github.mrbean355.admiralbulldog.ui.format
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

class DownloadUpdateViewModel(
        private val assetInfo: AssetInfo,
        private val destination: String,
        private val onDownloadComplete: () -> Unit
) {
    private val coroutineScope = CoroutineScope(Default + Job())
    private val gitHubRepository = GitHubRepository()
    private var loadedTotalSize = false

    val header = SimpleStringProperty("Downloading update...")
    val progress = SimpleDoubleProperty()
    val isComplete = SimpleBooleanProperty(false)

    fun init() {
        coroutineScope.launch {
            gitHubRepository.downloadAsset(assetInfo, destination) {
                withContext(Main) {
                    if (!loadedTotalSize) {
                        loadedTotalSize = true
                        val totalMegabytes = it.total / 1024.0 / 1024
                        val formatted = totalMegabytes.format(decimalPlaces = 2)
                        header.set("Downloading update ($formatted MB)...")
                    }
                    progress.set(it.current)
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
