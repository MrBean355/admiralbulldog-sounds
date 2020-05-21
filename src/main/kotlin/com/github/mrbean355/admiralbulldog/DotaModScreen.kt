package com.github.mrbean355.admiralbulldog

import com.github.mrbean355.admiralbulldog.ui.getString
import javafx.geometry.Pos.CENTER
import javafx.scene.text.Font
import tornadofx.View
import tornadofx.action
import tornadofx.checkbox
import tornadofx.enableWhen
import tornadofx.hyperlink
import tornadofx.label
import tornadofx.paddingAll
import tornadofx.spacer
import tornadofx.vbox
import tornadofx.whenUndocked

class DotaModScreen : View(getString("title_mod")) {
    private val viewModel by inject<DotaModViewModel>()

    override val root = vbox(spacing = PADDING_SMALL) {
        paddingAll = PADDING_MEDIUM
        label(getString("label_mod_info"))
        checkbox(getString("label_enable_mod"), viewModel.modEnabled)
        spacer {
            prefHeight = PADDING_SMALL
        }
        label(getString("label_temp_disable_description"))
        checkbox(getString("label_temp_disable"), viewModel.tempDisabled) {
            enableWhen(viewModel.modEnabled)
        }
        vbox(spacing = PADDING_SMALL, alignment = CENTER) {
            hyperlink(getString("btn_mod_info")) {
                action { hostServices.showDocument(URL_MOD_INFO) }
            }
            label(viewModel.formattedModVersion) {
                font = Font(TEXT_SIZE_SMALL)
            }
        }
    }

    init {
        whenUndocked {
            viewModel.onUndock()
        }
    }
}
