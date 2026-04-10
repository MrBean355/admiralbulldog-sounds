package com.github.mrbean355.admiralbulldog.game

import com.github.mrbean355.admiralbulldog.common.PADDING_MEDIUM
import com.github.mrbean355.admiralbulldog.common.PADDING_SMALL
import com.github.mrbean355.admiralbulldog.common.WINDOW_WIDTH
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.styles.AppStyles
import javafx.scene.control.ButtonBar.ButtonData.HELP
import tornadofx.Fragment
import tornadofx.Scope
import tornadofx.action
import tornadofx.addClass
import tornadofx.button
import tornadofx.buttonbar
import tornadofx.label
import tornadofx.paddingAll
import tornadofx.paddingTop
import tornadofx.vbox
import tornadofx.whenUndocked

class RoshanTimerScreen : Fragment(getString("header_roshan_timer")) {
    private val viewModel: RoshanTimerViewModel by inject(Scope())

    override val root = vbox(spacing = PADDING_SMALL) {
        prefWidth = WINDOW_WIDTH
        paddingAll = PADDING_MEDIUM

        label(viewModel.deathTime) {
            addClass(AppStyles.mediumFont)
        }
        label(viewModel.aegisExpiryTime) {
            addClass(AppStyles.mediumFont)
        }
        label(viewModel.respawnTime) {
            addClass(AppStyles.mediumFont)
        }

        label(getString("label_roshan_timer_turbo")) {
            paddingTop = PADDING_SMALL
            addClass(AppStyles.mediumFont)
            addClass(AppStyles.boldFont)
        }
        label(viewModel.aegisExpiryTimeTurbo) {
            addClass(AppStyles.mediumFont)
        }
        label(viewModel.respawnTimeTurbo) {
            addClass(AppStyles.mediumFont)
        }

        buttonbar {
            button(getString("btn_help"), type = HELP) {
                action {
                    viewModel.onHelpClicked()
                }
            }
        }
    }

    init {
        whenUndocked {
            viewModel.onUndock()
        }
    }
}
