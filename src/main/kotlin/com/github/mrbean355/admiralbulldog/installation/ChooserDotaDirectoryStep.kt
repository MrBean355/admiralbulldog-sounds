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

package com.github.mrbean355.admiralbulldog.installation

import com.github.mrbean355.admiralbulldog.AppStyles
import com.github.mrbean355.admiralbulldog.common.PADDING_MEDIUM
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.persistence.DotaPath
import javafx.beans.property.SimpleStringProperty
import tornadofx.Fragment
import tornadofx.action
import tornadofx.addClass
import tornadofx.button
import tornadofx.chooseDirectory
import tornadofx.fitToParentWidth
import tornadofx.label
import tornadofx.managedWhen
import tornadofx.vbox
import tornadofx.visibleWhen
import java.io.File

class ChooserDotaDirectoryStep : Fragment() {
    private val installationModel by inject<InstallationModel>()
    private var selectedDirectory: File? = null
    private var displayedPath = SimpleStringProperty(getString("install_no_path"))
    private val errorMessage = SimpleStringProperty(null)

    override val root = vbox(spacing = PADDING_MEDIUM) {
        label(getString("install_path_name"))
        label(displayedPath) {
            addClass(AppStyles.boldFont)
            isWrapText = true
            fitToParentWidth()
        }
        label(errorMessage) {
            visibleWhen(textProperty().isNotEmpty)
            managedWhen(visibleProperty())
            addClass(AppStyles.inlineError)
        }
        button(getString("install_choose")) {
            action(::onChooseClicked)
        }
    }

    private fun onChooseClicked() {
        selectedDirectory = chooseDirectory(
            title = getString("install_chooser_title"),
            initialDirectory = selectedDirectory,
            owner = currentWindow
        ) ?: return

        val verifiedPath = DotaPath.getDotaRootDirectory(selectedDirectory?.absolutePath.orEmpty())
        installationModel.dotaPath.set(verifiedPath)
        displayedPath.set(verifiedPath ?: selectedDirectory?.absolutePath)
        isComplete = (verifiedPath != null)
        errorMessage.set(if (isComplete) null else getString("install_invalid_path"))
    }

    init {
        isComplete = false
    }
}
