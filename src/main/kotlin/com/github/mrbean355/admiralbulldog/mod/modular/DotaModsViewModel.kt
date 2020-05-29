package com.github.mrbean355.admiralbulldog.mod.modular

import com.github.mrbean355.admiralbulldog.arch.DotaModRepository
import com.github.mrbean355.admiralbulldog.common.error
import com.github.mrbean355.admiralbulldog.common.getString
import javafx.beans.property.BooleanProperty
import javafx.beans.property.ObjectProperty
import javafx.scene.control.CheckBoxTreeItem
import javafx.scene.control.TreeItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tornadofx.FXEvent
import tornadofx.ViewModel
import tornadofx.booleanProperty
import tornadofx.objectProperty

class DotaModsViewModel : ViewModel() {
    private val coroutineScope = CoroutineScope(IO + SupervisorJob())
    private val repo = DotaModRepository()

    val showProgress: BooleanProperty = booleanProperty(true)
    val root: ObjectProperty<TreeItem<String>> = objectProperty()

    init {
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

            val root = TreeItem<String>()
            body.forEach { mod ->
                root.children += CheckBoxTreeItem(mod.name).apply {
                    children += mod.parts.map { CheckBoxTreeItem(it.name) }
                }
            }

            withContext(Main) {
                showProgress.set(false)
                this@DotaModsViewModel.root.set(root)
            }
        }
    }

    fun onSaveClicked() {
        val selection = mutableMapOf<String, List<String>>()
        root.get().children.forEach { mod ->
            selection[mod.value] = mod.children
                    .filterIsInstance<CheckBoxTreeItem<String>>()
                    .filter { it.isSelected }
                    .map { it.value }
        }
        // TODO: Compile VPK from selection
    }

    class CloseEvent : FXEvent()
}