package com.github.mrbean355.admiralbulldog

import com.github.mrbean355.admiralbulldog.arch.AssetInfo
import com.github.mrbean355.admiralbulldog.common.PADDING_MEDIUM
import com.github.mrbean355.admiralbulldog.common.PADDING_SMALL
import com.github.mrbean355.admiralbulldog.common.WINDOW_WIDTH
import com.github.mrbean355.admiralbulldog.ui.getString
import javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE
import tornadofx.FXEvent
import tornadofx.Fragment
import tornadofx.action
import tornadofx.button
import tornadofx.buttonbar
import tornadofx.fitToParentWidth
import tornadofx.label
import tornadofx.paddingAll
import tornadofx.progressbar
import tornadofx.vbox
import tornadofx.whenUndocked

class DownloadUpdateScreen : Fragment(getString("title_app")) {
    private val viewModel by inject<DownloadUpdateViewModel>(params = params)

    override val root = vbox(PADDING_SMALL) {
        paddingAll = PADDING_MEDIUM
        prefWidth = WINDOW_WIDTH
        label(viewModel.header)
        progressbar(viewModel.progress) {
            fitToParentWidth()
        }
        buttonbar {
            button(getString("btn_cancel"), type = CANCEL_CLOSE) {
                action { close() }
            }
        }
    }

    init {
        whenUndocked {
            viewModel.onUndock()
        }
        subscribe<DownloadUpdateViewModel.CloseEvent> {
            close()
            if (it.success) {
                fire(SuccessEvent())
            }
        }
    }

    class SuccessEvent : FXEvent()

    companion object {

        fun params(assetInfo: AssetInfo, destination: String): Map<String, Any?> {
            return mapOf("assetInfo" to assetInfo, "destination" to destination)
        }
    }
}
