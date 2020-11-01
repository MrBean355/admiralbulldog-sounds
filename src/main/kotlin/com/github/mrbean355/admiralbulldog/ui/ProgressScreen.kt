package com.github.mrbean355.admiralbulldog.ui

import com.github.mrbean355.admiralbulldog.AppStyles
import com.github.mrbean355.admiralbulldog.common.PADDING_LARGE
import com.github.mrbean355.admiralbulldog.common.PADDING_MEDIUM
import com.github.mrbean355.admiralbulldog.common.WINDOW_WIDTH_SMALL
import com.github.mrbean355.admiralbulldog.common.getString
import javafx.geometry.Pos.CENTER
import javafx.stage.StageStyle.UTILITY
import tornadofx.*

/** Displays an indeterminate progress bar with label. Cannot be closed by the user. */
class ProgressScreen : Fragment(getString("title_loading")) {

    override val root = vbox(spacing = PADDING_MEDIUM, alignment = CENTER) {
        prefWidth = WINDOW_WIDTH_SMALL
        paddingAll = PADDING_LARGE
        progressindicator()
        label(getString("label_loading")) {
            addClass(AppStyles.largeFont)
        }
    }
}

/** Show a progress screen which can't be closed by the user. */
fun Component.showProgressScreen(): ProgressScreen {
    return find<ProgressScreen>().apply {
        openModal(stageStyle = UTILITY, escapeClosesWindow = false, resizable = false)?.also { stage ->
            stage.setOnCloseRequest { it.consume() }
        }
    }
}