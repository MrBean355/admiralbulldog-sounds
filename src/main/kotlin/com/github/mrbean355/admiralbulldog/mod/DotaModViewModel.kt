package com.github.mrbean355.admiralbulldog.mod

import com.github.mrbean355.admiralbulldog.DownloadUpdateScreen
import com.github.mrbean355.admiralbulldog.arch.AppViewModel
import com.github.mrbean355.admiralbulldog.arch.GitHubRepository
import com.github.mrbean355.admiralbulldog.arch.ReleaseInfo
import com.github.mrbean355.admiralbulldog.arch.getModAssetInfo
import com.github.mrbean355.admiralbulldog.common.information
import com.github.mrbean355.admiralbulldog.common.logger
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import com.github.mrbean355.admiralbulldog.persistence.DotaMod
import com.github.mrbean355.admiralbulldog.ui.DotaPath
import com.github.mrbean355.admiralbulldog.ui.ProgressScreen
import com.github.mrbean355.admiralbulldog.ui.getString
import javafx.beans.property.BooleanProperty
import javafx.scene.control.ButtonType
import javafx.stage.StageStyle.UTILITY
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tornadofx.booleanProperty
import tornadofx.onChange
import tornadofx.stringBinding
import tornadofx.stringProperty
import java.io.File

class DotaModViewModel : AppViewModel() {
    private val gitHubRepository = GitHubRepository()
    private val modVersion = stringProperty(ConfigPersistence.getModVersion())

    val modEnabled: BooleanProperty = booleanProperty(ConfigPersistence.isModEnabled())
    val tempDisabled: BooleanProperty = booleanProperty(ConfigPersistence.isModTempDisabled())
    val formattedModVersion = modEnabled.stringBinding(modVersion) {
        val version = if (it == true) modVersion.get() else getString("label_not_applicable")
        getString("label_mod_version", version)
    }

    init {
        modEnabled.onChange { onEnabledCheckChanged(it) }
        tempDisabled.onChange { onTempDisabledCheckChanged(it) }
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

        val progressDialog = find<ProgressScreen>()
        progressDialog.openModal(stageStyle = UTILITY, escapeClosesWindow = false, resizable = false)?.also { stage ->
            stage.setOnCloseRequest { it.consume() }
        }

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
                    information(getString("header_mod_installed"), getString("msg_mod_restart_dota"), ButtonType.FINISH)
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
        subscribe<DownloadUpdateScreen.SuccessEvent>(times = 1) {
            information(getString("header_mod_update_downloaded"), getString("msg_mod_restart_dota"), ButtonType.FINISH)
        }
        find<DownloadUpdateScreen>(DownloadUpdateScreen.params(assetInfo, destination = DotaPath.getModDirectory()))
                .openModal(escapeClosesWindow = false, block = true, resizable = false)
    }

    private fun disableMod() {
        if (DotaMod.onModDisabled()) {
            information(getString("header_mod_uninstalled"), getString("msg_mod_restart_dota"), ButtonType.OK)
        }
    }
}