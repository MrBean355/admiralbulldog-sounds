package com.github.mrbean355.admiralbulldog.installation

import com.github.mrbean355.admiralbulldog.common.PADDING_SMALL
import com.github.mrbean355.admiralbulldog.common.WIKI_GAME_FILES
import com.github.mrbean355.admiralbulldog.ui.getString
import tornadofx.Fragment
import tornadofx.action
import tornadofx.hyperlink
import tornadofx.label
import tornadofx.textflow
import tornadofx.vbox

class ShowRationaleStep : Fragment() {

    override val root = vbox(spacing = PADDING_SMALL) {
        label(getString("install_rationale_1"))
        textflow {
            label(getString("install_rationale_2"))
            hyperlink(getString("install_rationale_3")) {
                action { hostServices.showDocument(WIKI_GAME_FILES) }
            }
        }
        label(getString("install_rationale_4"))
    }
}
