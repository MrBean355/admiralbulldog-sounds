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
    val root: ObjectProperty<TreeItem<ModTreeItem>?> = objectProperty()

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

            val root = TreeItem<ModTreeItem>()
            body.sortedBy { it.name.toLowerCase() }.forEach { mod ->
                root.children += CheckBoxTreeItem(ModTreeItem(mod)).apply {
                    children += mod.parts.map { CheckBoxTreeItem(ModTreeItem(mod, part = it)) }
                }
            }

            withContext(Main) {
                showProgress.set(false)
                this@DotaModsViewModel.root.set(root)
            }
        }
    }

    fun onSaveClicked() {
        val root = root.get() ?: return

        val selection = root.children
                .flatMap { it.children }
                .filterIsInstance<CheckBoxTreeItem<ModTreeItem>>()
                .filter { it.isSelected }
                .map { it.value }

        val mapped = selection.groupBy { it.mod }
                .mapValues { items -> items.value.map { it.part } }

        println(mapped)
        // TODO: Compile VPK from selection
    }

    class CloseEvent : FXEvent()
}