package com.github.mrbean355.admiralbulldog.mod.modular

import com.github.mrbean355.admiralbulldog.common.PADDING_MEDIUM
import com.github.mrbean355.admiralbulldog.common.PADDING_SMALL
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.common.useBoldFont
import javafx.geometry.Pos.CENTER
import javafx.scene.control.cell.CheckBoxTreeCell
import tornadofx.Fragment
import tornadofx.Scope
import tornadofx.action
import tornadofx.button
import tornadofx.fitToParentWidth
import tornadofx.label
import tornadofx.managedWhen
import tornadofx.onChange
import tornadofx.paddingAll
import tornadofx.progressbar
import tornadofx.treeview
import tornadofx.vbox
import tornadofx.visibleWhen

class DotaModsScreen : Fragment(getString("title_mod")) {
    private val viewModel by inject<DotaModsViewModel>(Scope())

    override val root = vbox(spacing = PADDING_SMALL) {
        paddingAll = PADDING_MEDIUM
        label(getString("label_choose_mod_parts")) {
            useBoldFont()
            alignment = CENTER
        }
        progressbar {
            fitToParentWidth()
            visibleWhen(viewModel.showProgress)
            managedWhen(visibleProperty())
        }
        treeview<String> {
            rootProperty().bind(viewModel.root)
            isShowRoot = false
            cellFactory = CheckBoxTreeCell.forTreeView()
        }
        button(getString("btn_save")) {
            action { viewModel.onSaveClicked() }
        }
    }

    init {
        viewModel.showProgress.onChange {
            currentStage?.sizeToScene()
        }
        subscribe<DotaModsViewModel.CloseEvent> {
            close()
        }
    }
}
