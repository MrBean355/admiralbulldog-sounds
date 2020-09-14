package com.github.mrbean355.admiralbulldog.mod

import com.github.mrbean355.admiralbulldog.AppStyles
import com.github.mrbean355.admiralbulldog.common.PADDING_MEDIUM
import com.github.mrbean355.admiralbulldog.common.PADDING_SMALL
import com.github.mrbean355.admiralbulldog.common.WINDOW_WIDTH
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.mod.modular.DotaModsScreen
import javafx.geometry.Pos.CENTER
import tornadofx.Fragment
import tornadofx.action
import tornadofx.addClass
import tornadofx.button
import tornadofx.fitToParentWidth
import tornadofx.label
import tornadofx.paddingAll
import tornadofx.vbox

class ChooseModTypeScreen : Fragment(getString("title_mod")) {

    override val root = vbox(spacing = PADDING_SMALL, alignment = CENTER) {
        paddingAll = PADDING_MEDIUM
        prefWidth = WINDOW_WIDTH
        label(getString("label_choose_mod_system")) {
            addClass(AppStyles.boldFont)
        }
        button(getString("btn_mod_system_legacy")) {
            fitToParentWidth()
            action {
                close()
                find<DotaModScreen>().openModal(resizable = false)
            }
        }
        button(getString("btn_mod_system_modular")) {
            fitToParentWidth()
            action {
                close()
                find<DotaModsScreen>().openModal(resizable = false)
            }
        }
    }
}