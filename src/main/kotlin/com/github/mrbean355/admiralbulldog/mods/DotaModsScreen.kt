package com.github.mrbean355.admiralbulldog.mods

import com.github.mrbean355.admiralbulldog.AppStyles
import com.github.mrbean355.admiralbulldog.common.*
import javafx.geometry.Pos.CENTER
import tornadofx.*

class DotaModsScreen : Fragment(getString("title_mods")) {
    private val viewModel by inject<DotaModsViewModel>(Scope())

    override val root = vbox(spacing = PADDING_SMALL) {
        paddingAll = PADDING_MEDIUM
        prefWidth = WINDOW_WIDTH
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
            hyperlink(getString("btn_more_info")) {
                action { viewModel.onMoreInformationClicked() }
            }
            spacer()
            label(getString("label_select_mods")) {
                paddingTop = 3
                paddingRight = 4
            }
            hyperlink(getString("btn_select_all")) {
                action { viewModel.onSelectAllClicked() }
            }
            hyperlink(getString("btn_deselect_all")) {
                action { viewModel.onDeselectAllClicked() }
            }
        }
        button(getString("btn_save")) {
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
