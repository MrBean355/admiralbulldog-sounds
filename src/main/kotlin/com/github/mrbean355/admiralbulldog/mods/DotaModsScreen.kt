package com.github.mrbean355.admiralbulldog.mods

import com.github.mrbean355.admiralbulldog.AppStyles
import com.github.mrbean355.admiralbulldog.arch.DotaMod
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
                    onButtonClicked = this@DotaModsScreen::showModInfo
            )
        }
        button(getString("btn_save")) {
            action { viewModel.onSaveClicked() }
        }
        hyperlink(getString("btn_more_info")) {
            action { viewModel.onMoreInformationClicked() }
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

    private fun showModInfo(mod: DotaMod) {
        showInformation(mod.name, """
            ${mod.description}
            ${getString("label_mod_download_size", getDownloadSize(mod))}
        """.trimIndent())
    }

    private fun getDownloadSize(mod: DotaMod): String {
        val kb = mod.size / 1024.0
        return if (kb >= 1024.0) {
            "%.1f MB".format(kb / 1024.0)
        } else {
            "%.1f KB".format(kb)
        }
    }
}
