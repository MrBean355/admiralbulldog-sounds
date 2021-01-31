/*
 * Copyright 2021 Michael Johnston
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.mrbean355.admiralbulldog.ui

import com.github.mrbean355.admiralbulldog.AppStyles
import com.github.mrbean355.admiralbulldog.common.PADDING_LARGE
import com.github.mrbean355.admiralbulldog.common.PADDING_MEDIUM
import com.github.mrbean355.admiralbulldog.common.WINDOW_WIDTH_SMALL
import com.github.mrbean355.admiralbulldog.common.getString
import javafx.geometry.Pos.CENTER
import javafx.stage.StageStyle.UTILITY
import tornadofx.Component
import tornadofx.Fragment
import tornadofx.addClass
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