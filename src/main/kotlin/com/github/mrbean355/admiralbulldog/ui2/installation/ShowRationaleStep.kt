package com.github.mrbean355.admiralbulldog.ui2.installation

import com.github.mrbean355.admiralbulldog.ui.getString
import com.github.mrbean355.admiralbulldog.ui2.Spacing
import com.github.mrbean355.admiralbulldog.ui2.WIKI_GAME_FILES
import tornadofx.Fragment
import tornadofx.action
import tornadofx.hyperlink
import tornadofx.label
import tornadofx.textflow
import tornadofx.vbox

class ShowRationaleStep : Fragment() {

    override val root = vbox(spacing = Spacing.SMALL) {
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
