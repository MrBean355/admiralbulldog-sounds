package com.github.mrbean355.admiralbulldog.mods

import com.github.mrbean355.admiralbulldog.common.HelpIcon
import com.github.mrbean355.admiralbulldog.common.PADDING_MEDIUM
import com.github.mrbean355.admiralbulldog.common.PADDING_SMALL
import com.github.mrbean355.admiralbulldog.common.WINDOW_WIDTH
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.common.useCheckBoxWithButton
import com.github.mrbean355.admiralbulldog.styles.AppStyles
import javafx.geometry.Pos.CENTER
import tornadofx.Fragment
import tornadofx.Scope
import tornadofx.action
import tornadofx.addClass
import tornadofx.button
import tornadofx.fitToParentWidth
import tornadofx.hbox
import tornadofx.hyperlink
import tornadofx.label
import tornadofx.listview
import tornadofx.managedWhen
import tornadofx.onChange
import tornadofx.paddingAll
import tornadofx.paddingRight
import tornadofx.paddingTop
import tornadofx.progressbar
import tornadofx.spacer
import tornadofx.vbox
import tornadofx.visibleWhen
import tornadofx.whenUndocked

class DotaModsScreen : Fragment(getString("title_mods")) {
    private val viewModel by inject<DotaModsViewModel>(Scope())

    override val root = vbox(spacing = PADDING_SMALL) {
        paddingAll = PADDING_MEDIUM
        prefWidth = WINDOW_WIDTH
        hbox(alignment = CENTER) {
            label(getString("label_enable_mods_manually"))
            spacer()
            button(getString("btn_enable")) {
                action { viewModel.onEnableClicked() }
            }
        }
        label(getString("label_choose_mods")) {
            addClass(AppStyles.boldFont)
            alignment = CENTER
        }
        progressbar {
            fitToParentWidth()
            visibleWhen(viewModel.showProgress)
            managedWhen(visibleProperty())
        }
        listview(viewModel.items) {
            useCheckBoxWithButton(
                buttonImage = HelpIcon(),
                buttonTooltip = getString("tooltip_more_info"),
                getSelectedProperty = viewModel::getCheckedProperty,
                stringConverter = { it.name },
                onButtonClicked = viewModel::onAboutModClicked
            )
        }
        hbox {
            label(getString("label_select")) {
                paddingTop = 3
                paddingRight = 4
            }
            hyperlink(getString("btn_select_all")) {
                action { viewModel.onSelectAllClicked() }
            }
            hyperlink(getString("btn_deselect_all")) {
                action { viewModel.onDeselectAllClicked() }
            }
            spacer()
            hyperlink(getString("btn_about_modding")) {
                action { viewModel.onAboutModdingClicked() }
            }
        }
        button(getString("btn_download")) {
            action { viewModel.onInstallClicked() }
        }
    }

    init {
        viewModel.showProgress.onChange {
            currentStage?.sizeToScene()
        }
        subscribe<DotaModsViewModel.CloseEvent> {
            close()
        }
        whenUndocked {
            viewModel.onUndock()
        }
    }
}
