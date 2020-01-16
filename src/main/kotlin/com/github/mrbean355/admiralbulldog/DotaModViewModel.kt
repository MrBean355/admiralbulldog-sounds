package com.github.mrbean355.admiralbulldog

import com.github.mrbean355.admiralbulldog.arch.GitHubRepository
import com.github.mrbean355.admiralbulldog.arch.ReleaseInfo
import com.github.mrbean355.admiralbulldog.arch.getModAssetInfo
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import com.github.mrbean355.admiralbulldog.persistence.DotaMod
import com.github.mrbean355.admiralbulldog.ui.Alert
import com.github.mrbean355.admiralbulldog.ui.DotaPath
import com.github.mrbean355.admiralbulldog.ui.ProgressDialog
import com.github.mrbean355.admiralbulldog.ui.getString
import com.github.mrbean355.admiralbulldog.ui.removeVersionPrefix
import com.github.mrbean355.admiralbulldog.ui.showModal
import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.stage.Stage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import java.io.File

class DotaModViewModel(private val stage: Stage) {
    private val logger = LoggerFactory.getLogger(DotaModViewModel::class.java)
    private val gitHubRepository = GitHubRepository()
    private val coroutineScope = CoroutineScope(Default + Job())

    val modEnabled = SimpleBooleanProperty(ConfigPersistence.isModEnabled()).apply {
        addListener { _, _, newValue -> onEnabledCheckChanged(newValue) }
    }

    fun onClose() {
        coroutineScope.cancel()
    }

    private fun onEnabledCheckChanged(checked: Boolean) {
        ConfigPersistence.setModEnabled(checked)
        if (checked) {
            installMod()
        } else {
            uninstallMod()
        }
    }

    private fun installMod() {
        val modDirectory = File(DotaPath.getModDirectory())
        logger.info("Installing mod in: ${modDirectory.absolutePath}")
        modDirectory.mkdirs()
        DotaMod.installIntoGameInfo()

        val progressDialog = ProgressDialog()
        progressDialog.showModal(stage)

        coroutineScope.launch {
            logger.info("Checking for mod update...")
            val resource = gitHubRepository.getLatestModRelease()
            val releaseInfo = resource.body()
            if (!resource.isSuccessful || releaseInfo == null) {
                logger.warn("Bad mod release info response, giving up")
                withContext(Main) {
                    progressDialog.close()
                }
                return@launch
            }
            val shouldDownloadUpdate = DotaMod.shouldDownloadUpdate(releaseInfo)
            withContext(Main) {
                progressDialog.close()
            }
            if (shouldDownloadUpdate) {
                logger.info("Later mod version available: ${releaseInfo.tagName}")
                withContext(Main) {
                    downloadModUpdate(releaseInfo)
                }
            } else {
                logger.info("Already at latest mod version: ${releaseInfo.tagName}")
                withContext(Main) {
                    Alert(type = Alert.AlertType.INFORMATION,
                            header = "Dota mod installed",
                            content = getString("msg_mod_update_downloaded"),
                            buttons = arrayOf(ButtonType.FINISH),
                            owner = stage
                    ).showAndWait()
                }
            }
        }
    }

    private fun downloadModUpdate(releaseInfo: ReleaseInfo) {
        val assetInfo = releaseInfo.getModAssetInfo()
        if (assetInfo == null) {
            logger.warn("Bad mod asset info, giving up")
            return
        }
        DownloadUpdateStage(assetInfo, destination = DotaPath.getModDirectory()) {
            ConfigPersistence.setModVersion(releaseInfo.tagName.removeVersionPrefix())
            Alert(type = Alert.AlertType.INFORMATION,
                    header = getString("header_mod_update_downloaded"),
                    content = getString("msg_mod_update_downloaded"),
                    buttons = arrayOf(ButtonType.FINISH),
                    owner = stage
            ).showAndWait()
        }.showModal(stage)
    }

    private fun uninstallMod() {
        DotaMod.uninstallFromGameInfo()
        Alert(type = Alert.AlertType.INFORMATION,
                header = "Dota mod uninstalled",
                content = "Please restart Dota if it's open.",
                buttons = arrayOf(ButtonType.OK),
                owner = stage
        ).show()
    }
}