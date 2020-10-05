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
            items.setAll(body.sortedBy { it.name.toLowerCase() })
        }
    }

    fun getCheckedProperty(dotaMod: DotaMod): BooleanProperty {
        return checkedProperties.getOrPut(dotaMod.name) {
            booleanProperty(ConfigPersistence.isModEnabled(dotaMod))
        }
    }

    fun onSaveClicked() {
        val enabledMods = items.filter {
            getCheckedProperty(it).value
        }

        viewModelScope.launch {
            downloadMods(enabledMods)
        }
    }

    fun onMoreInformationClicked() {
        hostServices.showDocument(URL_MOD_INFO)
    }

    private suspend fun downloadMods(mods: Collection<DotaMod>) {
        val progressScreen = showProgressScreen()
        val allSucceeded = repo.installMods(mods)
        progressScreen.close()
        if (allSucceeded) {
            showInformation(getString("header_mod_updates_succeeded"), getString("content_mod_updates_succeeded"))
        } else {
            showWarning(getString("header_mod_updates_failed"), getString("content_mod_updates_failed"), RETRY_BUTTON, ButtonType.CANCEL) {
                if (it === RETRY_BUTTON) {
                    downloadMods(mods)
                }
            }
        }
    }

    class CloseEvent : FXEvent()
}