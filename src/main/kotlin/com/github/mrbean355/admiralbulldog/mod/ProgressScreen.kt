package com.github.mrbean355.admiralbulldog.mod

import com.github.mrbean355.admiralbulldog.common.PADDING_LARGE
import com.github.mrbean355.admiralbulldog.common.PADDING_MEDIUM
import com.github.mrbean355.admiralbulldog.common.WINDOW_WIDTH_SMALL
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.common.useLargeFont
import javafx.geometry.Pos.CENTER
import tornadofx.Fragment
import tornadofx.label
import tornadofx.paddingAll
import tornadofx.progressindicator
import tornadofx.vbox

/** Displays an indeterminate progress bar with label. Cannot be closed by the user. */
class ProgressScreen : Fragment(getString("title_loading")) {

    override val root = vbox(spacing = PADDING_MEDIUM, alignment = CENTER) {
        prefWidth = WINDOW_WIDTH_SMALL
        paddingAll = PADDING_LARGE
        progressindicator()
        label(getString("label_loading")) {
            useLargeFont()
        }
    }
}