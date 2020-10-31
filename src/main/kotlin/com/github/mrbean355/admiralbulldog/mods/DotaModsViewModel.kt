package com.github.mrbean355.admiralbulldog.mods

import com.github.mrbean355.admiralbulldog.arch.AppViewModel
import com.github.mrbean355.admiralbulldog.arch.DotaMod
import com.github.mrbean355.admiralbulldog.arch.repo.DotaModRepository
import com.github.mrbean355.admiralbulldog.common.*
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import com.github.mrbean355.admiralbulldog.ui.showProgressScreen
import javafx.beans.property.BooleanProperty
import javafx.collections.ObservableList
import javafx.scene.control.ButtonType
import javafx.scene.control.ButtonType.CLOSE
import kotlinx.coroutines.launch
import tornadofx.*

class DotaModsViewModel : AppViewModel() {
    private val repo = DotaModRepository()
    private val checkedProperties = mutableMapOf<String, BooleanProperty>()

    val showProgress: BooleanProperty = booleanProperty(true)
    val items: ObservableList<DotaMod> = observableListOf()

    override fun onReady() {
        viewModelScope.launch {
            val response = repo.listMods()
            val body = response.body
            if (!response.isSuccessful() || body == null) {
                showError(getString("title_unknown_error"), getString("msg_mod_list_failure"))
                fire(CloseEvent())
                return@launch
            }
            showProgress.set(false)
            items.setAll(body.sortedWith(DotaModComparator()))
        }
    }

    fun getCheckedProperty(dotaMod: DotaMod): BooleanProperty {
        return checkedProperties.getOrPut(dotaMod.name) {
            booleanProperty(ConfigPersistence.isModEnabled(dotaMod))
        }
    }

    fun onAboutModClicked(mod: DotaMod) {
        showInformation(mod.name, """
            ${mod.description}
            
            ${getString("label_mod_download_size", getDownloadSize(mod))}
        """.trimIndent(), MORE_INFO_BUTTON, CLOSE) {
            if (it === MORE_INFO_BUTTON) {
                hostServices.showDocument(mod.infoUrl)
            }
        }
    }

    fun onSelectAllClicked() {
        items.forEach {
            getCheckedProperty(it).set(true)
        }
    }

    fun onDeselectAllClicked() {
        items.forEach {
            getCheckedProperty(it).set(false)
        }
    }

    fun onInstallClicked() {
        val enabledMods = items.filter {
            getCheckedProperty(it).value
        }

        viewModelScope.launch {
            downloadMods(enabledMods)
        }
    }

    fun onAboutModdingClicked() {
        hostServices.showDocument(URL_MOD_INFO)
    }

    private suspend fun downloadMods(mods: Collection<DotaMod>) {
        val progressScreen = showProgressScreen()
        val allSucceeded = repo.installMods(mods)
        progressScreen.close()
        if (allSucceeded) {
            if (mods.isEmpty()) {
                showInformation(getString("header_mods_uninstalled_succeeded"), getString("content_mods_uninstalled_succeeded"))
            } else {
                showInformation(getString("header_mod_updates_succeeded"), getString("content_mod_updates_succeeded"))
            }
        } else {
            showWarning(getString("header_mod_updates_failed"), getString("content_mod_updates_failed"), RETRY_BUTTON, ButtonType.CANCEL) {
                if (it === RETRY_BUTTON) {
                    downloadMods(mods)
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

    class CloseEvent : FXEvent()
}