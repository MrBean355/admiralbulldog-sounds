package com.github.mrbean355.admiralbulldog.mods

import com.github.mrbean355.admiralbulldog.arch.ComposeViewModel
import java.awt.Desktop
import java.net.URI
import com.github.mrbean355.admiralbulldog.arch.DotaMod
import com.github.mrbean355.admiralbulldog.arch.repo.DotaModRepository
import com.github.mrbean355.admiralbulldog.common.AlertButton
import com.github.mrbean355.admiralbulldog.common.URL_MOD_INFO
import com.github.mrbean355.admiralbulldog.common.URL_MOD_LAUNCH_OPTION
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.common.showError
import com.github.mrbean355.admiralbulldog.common.showInformation
import com.github.mrbean355.admiralbulldog.common.showWarning
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DotaModsViewModel : ComposeViewModel() {
    private val repo = DotaModRepository()
    private val checkedProperties = mutableMapOf<String, MutableStateFlow<Boolean>>()

    private val _showProgress = MutableStateFlow(true)
    val showProgress = _showProgress.asStateFlow()

    private val _items = MutableStateFlow<List<DotaMod>>(emptyList())
    val items = _items.asStateFlow()

    private val _isUpdating = MutableStateFlow(false)
    val isUpdating = _isUpdating.asStateFlow()

    private val _showRiskDisclaimer = MutableStateFlow(!ConfigPersistence.isModRiskAccepted())
    val showRiskDisclaimer = _showRiskDisclaimer.asStateFlow()

    init {
        if (ConfigPersistence.isModRiskAccepted()) {
            loadMods()
        }
    }

    fun onRiskAccepted() {
        ConfigPersistence.setModRiskAccepted()
        _showRiskDisclaimer.value = false
        loadMods()
    }

    fun onRiskRejected() {
        requestWindowClose()
    }

    private fun loadMods() {
        viewModelScope.launch {
            val response = repo.listMods()
            val body = response.body
            if (!response.isSuccessful() || body == null) {
                showError(getString("header_unknown_error"), getString("content_mod_list_failure"))
                requestWindowClose()
                return@launch
            }
            _showProgress.value = false
            _items.value = body.sortedWith(DotaModComparator())
        }
    }

    fun getCheckedState(dotaMod: DotaMod): MutableStateFlow<Boolean> {
        return checkedProperties.getOrPut(dotaMod.name) {
            MutableStateFlow(ConfigPersistence.isModEnabled(dotaMod))
        }
    }

    fun onAboutModClicked(mod: DotaMod) {
        showInformation(
            mod.name,
            """
            ${mod.description}
            
            ${getString("label_mod_download_size", getDownloadSize(mod))}
            """.trimIndent(), AlertButton.MORE_INFO, AlertButton.OK
        ) {
            if (it == AlertButton.MORE_INFO) {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().browse(URI(mod.infoUrl))
                }
            }
        }
    }

    fun onSelectAllClicked() {
        items.value.forEach {
            getCheckedState(it).value = true
        }
    }

    fun onDeselectAllClicked() {
        items.value.forEach {
            getCheckedState(it).value = false
        }
    }

    fun onInstallClicked() {
        showInformation(getString("header_close_dota"), getString("content_close_dota"), AlertButton.CANCEL, AlertButton.NEXT) { action ->
            if (action == AlertButton.NEXT) {
                val enabledMods = items.value.filter {
                    getCheckedState(it).value
                }

                viewModelScope.launch {
                    downloadMods(enabledMods)
                }
            }
        }
    }

    fun onEnableClicked() {
        showInformation(getString("header_mod_launch_option"), getString("content_mod_launch_option"), AlertButton.MORE_INFO, AlertButton.OK) {
            if (it == AlertButton.MORE_INFO) {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().browse(URI(URL_MOD_LAUNCH_OPTION))
                }
            }
        }
    }

    fun onAboutModdingClicked() {
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().browse(URI(URL_MOD_INFO))
        }
    }

    private suspend fun downloadMods(mods: Collection<DotaMod>) {
        _isUpdating.value = true
        val allSucceeded = repo.installMods(mods)
        _isUpdating.value = false
        if (allSucceeded) {
            if (mods.isEmpty()) {
                showInformation(getString("header_mods_remove_succeeded"), getString("content_mods_remove_succeeded"))
            } else {
                showInformation(getString("header_mod_updates_succeeded"), getString("content_mod_updates_succeeded"))
            }
        } else {
            showWarning(getString("header_mod_updates_failed"), getString("content_mod_updates_failed"), AlertButton.RETRY, AlertButton.CANCEL) {
                if (it == AlertButton.RETRY) {
                    viewModelScope.launch {
                        downloadMods(mods)
                    }
                }
            }
        }
    }

    private fun getDownloadSize(mod: DotaMod): String {
        val kb = mod.size.toFloat()
        return if (kb >= 1024.0) {
            "%.1f MB".format(kb / 1024.0)
        } else {
            "%.1f KB".format(kb)
        }
    }
}