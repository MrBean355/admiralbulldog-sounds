package com.github.mrbean355.admiralbulldog.mods

import com.github.mrbean355.admiralbulldog.common.MonkaGigaIcon
import com.github.mrbean355.admiralbulldog.common.PADDING_MEDIUM
import com.github.mrbean355.admiralbulldog.common.PADDING_SMALL
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import com.github.mrbean355.admiralbulldog.styles.AppStyles
import javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE
import javafx.scene.control.ButtonBar.ButtonData.OK_DONE
import tornadofx.Fragment
import tornadofx.action
import tornadofx.addClass
import tornadofx.booleanProperty
import tornadofx.button
import tornadofx.buttonbar
import tornadofx.checkbox
import tornadofx.enableWhen
import tornadofx.hbox
import tornadofx.imageview
import tornadofx.label
import tornadofx.paddingAll
import tornadofx.paddingLeft
import tornadofx.vbox

class AcceptModRiskScreen : Fragment(getString("title_mods_risk")) {

    override val root = vbox(spacing = PADDING_SMALL) {
        paddingAll = PADDING_MEDIUM
        hbox {
            imageview(MonkaGigaIcon())
            label(getString("description_mods_risk")) {
                addClass(AppStyles.mediumFont)
                paddingLeft = PADDING_MEDIUM
            }
        }
        val accepted = booleanProperty()
        checkbox(getString("label_accept_mods_risk"), accepted) {
            addClass(AppStyles.mediumFont)
        }
        buttonbar {
            button(getString("btn_done"), OK_DONE) {
                enableWhen(accepted)
                action {
                    ConfigPersistence.setModRiskAccepted()
                    close()
                }
            }
            button(getString("btn_cancel"), CANCEL_CLOSE) {
                action { close() }
            }
        }
    }
}