package com.github.mrbean355.admiralbulldog.mod.modular

import com.github.mrbean355.admiralbulldog.arch.AppViewModel
import com.github.mrbean355.admiralbulldog.arch.DotaMod
import com.github.mrbean355.admiralbulldog.arch.repo.DotaModRepository
import com.github.mrbean355.admiralbulldog.common.error
import com.github.mrbean355.admiralbulldog.common.getString
import javafx.beans.property.BooleanProperty
import javafx.collections.ObservableList
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tornadofx.*

class DotaModsViewModel : AppViewModel() {
    private val repo = DotaModRepository()
    private val checkedProperties = mutableMapOf<String, BooleanProperty>()

    val showProgress: BooleanProperty = booleanProperty(true)
    val items: ObservableList<DotaMod> = observableListOf()

    override fun onReady() {
        coroutineScope.launch {
            val response = repo.listMods()
            val body = response.body
            if (!response.isSuccessful() || body == null) {
                withContext(Main) {
                    error(getString("title_unknown_error"), getString("msg_mod_list_failure"))
                    fire(CloseEvent())
                }
                return@launch
            }

            withContext(Main) {
                showProgress.set(false)
                items.setAll(body.sortedBy { it.name.toLowerCase() })
            }
        }
    }

    fun getCheckedProperty(dotaMod: DotaMod): BooleanProperty {
        return checkedProperties.getOrPut(dotaMod.name) { booleanProperty(false) }
    }

    fun onSaveClicked() {
        val selection = items.filter {
            checkedProperties[it.name]?.value ?: false
        }

        // TODO: Download & install enabled mods.
        // TODO: Uninstall disabled mods.
    }

    class CloseEvent : FXEvent()
}