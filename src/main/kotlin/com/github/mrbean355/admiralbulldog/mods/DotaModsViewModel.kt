/*
 * Copyright 2022 Michael Johnston
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.mrbean355.admiralbulldog.mods

import com.github.mrbean355.admiralbulldog.arch.AppViewModel
import com.github.mrbean355.admiralbulldog.arch.DotaMod
import com.github.mrbean355.admiralbulldog.arch.repo.DotaModRepository
import com.github.mrbean355.admiralbulldog.common.MORE_INFO_BUTTON
import com.github.mrbean355.admiralbulldog.common.RETRY_BUTTON
import com.github.mrbean355.admiralbulldog.common.URL_MOD_INFO
import com.github.mrbean355.admiralbulldog.common.URL_MOD_LAUNCH_OPTION
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.common.showError
import com.github.mrbean355.admiralbulldog.common.showInformation
import com.github.mrbean355.admiralbulldog.common.showWarning
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import com.github.mrbean355.admiralbulldog.ui.openScreen
import com.github.mrbean355.admiralbulldog.ui.showProgressScreen
import javafx.beans.property.BooleanProperty
import javafx.collections.ObservableList
import javafx.scene.control.ButtonType.CANCEL
import javafx.scene.control.ButtonType.CLOSE
import javafx.scene.control.ButtonType.NEXT
import kotlinx.coroutines.launch
import tornadofx.FXEvent
import tornadofx.booleanProperty
import tornadofx.observableListOf

class DotaModsViewModel : AppViewModel() {
    private val repo = DotaModRepository()
    private val checkedProperties = mutableMapOf<String, BooleanProperty>()

    val showProgress: BooleanProperty = booleanProperty(true)
    val items: ObservableList<DotaMod> = observableListOf()

    override fun onReady() {
        if (!ConfigPersistence.isModRiskAccepted()) {
            openScreen<AcceptModRiskScreen>(block = true)
            if (!ConfigPersistence.isModRiskAccepted()) {
                fire(CloseEvent())
                return
            }
        }
        viewModelScope.launch {
            val response = repo.listMods()
            val body = response.body
            if (!response.isSuccessful() || body == null) {
                showError(getString("header_unknown_error"), getString("content_mod_list_failure"))
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
        showInformation(
            mod.name,
            """
            ${mod.description}
            
            ${getString("label_mod_download_size", getDownloadSize(mod))}
            """.trimIndent(), MORE_INFO_BUTTON, CLOSE
        ) {
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
        showInformation(getString("header_close_dota"), getString("content_close_dota"), CANCEL, NEXT) { action ->
            if (action === NEXT) {
                val enabledMods = items.filter {
                    getCheckedProperty(it).value
                }

                viewModelScope.launch {
                    downloadMods(enabledMods)
                }
            }
        }
    }

    fun onEnableClicked() {
        showInformation(getString("header_mod_launch_option"), getString("content_mod_launch_option"), MORE_INFO_BUTTON, CLOSE) {
            if (it === MORE_INFO_BUTTON) {
                hostServices.showDocument(URL_MOD_LAUNCH_OPTION)
            }
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
                showInformation(getString("header_mods_remove_succeeded"), getString("content_mods_remove_succeeded"))
            } else {
                showInformation(getString("header_mod_updates_succeeded"), getString("content_mod_updates_succeeded"))
            }
        } else {
            showWarning(getString("header_mod_updates_failed"), getString("content_mod_updates_failed"), RETRY_BUTTON, CANCEL) {
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