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
import com.github.mrbean355.admiralbulldog.ui.showModal
import javafx.beans.binding.Bindings.createStringBinding
import javafx.beans.binding.StringBinding
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
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
import java.util.concurrent.Callable

class DotaModViewModel(private val stage: Stage) {
    private val logger = LoggerFactory.getLogger(DotaModViewModel::class.java)
    private val gitHubRepository = GitHubRepository()
    private val coroutineScope = CoroutineScope(Default + Job())

    private val _modVersion = SimpleStringProperty(ConfigPersistence.getModVersion())
    val modEnabled = SimpleBooleanProperty(ConfigPersistence.isModEnabled()).apply {
        addListener { _, _, newValue -> onEnabledCheckChanged(newValue) }
    }
    val tempDisabled = SimpleBooleanProperty(ConfigPersistence.isModTempDisabled()).apply {
        addListener { _, _, newValue -> onTempDisabledCheckChanged(newValue) }
    }
    val modVersion: StringBinding = createStringBinding(Callable {
        val version = if (modEnabled.get()) _modVersion.get() else "N/A"
        getString("label_mod_version", version)
    }, modEnabled, _modVersion)

    fun onClose() {
        coroutineScope.cancel()
    }

    private fun onEnabledCheckChanged(checked: Boolean) {
        ConfigPersistence.setModEnabled(checked)
        checkModStatus()
    }

    private fun onTempDisabledCheckChanged(checked: Boolean) {
        ConfigPersistence.setModTempDisabled(checked)
        checkModStatus()
    }

    private fun checkModStatus() {
        val enabled = modEnabled.get()
        val tempDisabled = tempDisabled.get()
        if (enabled && !tempDisabled) {
            installMod()
        } else {
            disableMod()
        }
    }

    private fun installMod() {
        val modDirectory = File(DotaPath.getModDirectory())
        logger.info("Installing mod in: ${modDirectory.absolutePath}")
        modDirectory.mkdirs()
        DotaMod.onModEnabled()

        val progressDialog = ProgressDialog()
        progressDialog.showModal(stage)

        coroutineScope.launch {
            logger.info("Checking for mod update...")
            val resource = gitHubRepository.getLatestModRelease()
            val releaseInfo = resource.body
            if (!resource.isSuccessful() || releaseInfo == null) {
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
        // TODO: Uncomment me
//        subscribe<DownloadUpdateScreen.SuccessEvent>(times = 1) {
//            information(
//                    header = getString("header_mod_update_downloaded"),
//                    content = getString("msg_mod_update_downloaded"),
//                    buttons = *arrayOf(ButtonType.FINISH)
//            )
//        }
//        find<DownloadUpdateScreen>(DownloadUpdateScreen.params(assetInfo, destination = DotaPath.getModDirectory()))
//                .openModal(escapeClosesWindow = false, block = true, resizable = false)
    }

    private fun disableMod() {
        if (DotaMod.onModDisabled()) {
            Alert(type = Alert.AlertType.INFORMATION,
                    header = "Dota mod uninstalled",
                    content = "Please restart Dota if it's open.",
                    buttons = arrayOf(ButtonType.OK),
                    owner = stage
            ).show()
        }
    }
}