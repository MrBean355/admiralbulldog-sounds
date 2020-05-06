package com.github.mrbean355.admiralbulldog.ui2.update

import com.github.mrbean355.admiralbulldog.APP_VERSION
import com.github.mrbean355.admiralbulldog.arch.GitHubRepository
import com.github.mrbean355.admiralbulldog.arch.ReleaseInfo
import com.github.mrbean355.admiralbulldog.arch.getAppAssetInfo
import com.github.mrbean355.admiralbulldog.ui.format
import com.github.mrbean355.admiralbulldog.ui.getString
import com.github.mrbean355.admiralbulldog.ui.removeVersionPrefix
import com.github.mrbean355.admiralbulldog.ui.streamToFile
import com.github.mrbean355.admiralbulldog.ui2.persistance.AppConfig
import com.vdurmont.semver4j.Semver
import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.scene.control.ProgressBar.INDETERMINATE_PROGRESS
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlinx.coroutines.withContext
import tornadofx.FXEvent
import tornadofx.ViewModel
import java.io.File

class CheckForUpdateViewModel : ViewModel() {
    private val coroutineScope = CoroutineScope(IO) + SupervisorJob()
    private val gitHubRepository = GitHubRepository()
    private var releaseInfo: ReleaseInfo? = null

    val description: StringProperty = SimpleStringProperty(getString("label_download_pending"))
    val progress: DoubleProperty = SimpleDoubleProperty(INDETERMINATE_PROGRESS)

    fun initialise() {
        checkForUpdate()
    }

    fun onRetryClicked() {
        checkForUpdate()
    }

    fun onDownloadClicked() {
        val assetInfo = releaseInfo?.getAppAssetInfo() ?: return
        description.set(getString("label_download_starting"))
        coroutineScope.launch {
            val response = gitHubRepository.downloadAsset(assetInfo)
            val body = response.body
            if (!response.isSuccessful() || body == null) {
                return@launch
            }
            val totalBytes = body.contentLength().toDouble()
            withContext(Main) {
                description.set(getString("label_download_progress", (totalBytes / 1024 / 1024).format(decimalPlaces = 2)))
            }
            body.byteStream().use { input ->
                input.streamToFile(File(assetInfo.name)) { bytes ->
                    withContext(Main) {
                        progress.set(bytes / totalBytes)
                    }
                }
            }
            fire(DownloadCompleteEvent(assetInfo.name))
        }
    }

    private fun checkForUpdate() {
        coroutineScope.launch {
            val response = gitHubRepository.getLatestAppRelease()
            val body = response.body
            if (!response.isSuccessful() || body == null) {
                fire(ErrorEvent())
                return@launch
            }
            releaseInfo = body
            val latestVersion = Semver(body.tagName.removeVersionPrefix())
            if (latestVersion > APP_VERSION) {
                fire(NewVersionEvent(latestVersion))
            } else {
                fire(UpToDateEvent())
            }
            AppConfig.setLastUpdateCheckToNow()
        }
    }

    class ErrorEvent : FXEvent()
    class NewVersionEvent(val version: Semver) : FXEvent()
    class UpToDateEvent : FXEvent()
    class DownloadCompleteEvent(val fileName: String) : FXEvent()
}