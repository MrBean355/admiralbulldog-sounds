package com.github.mrbean355.admiralbulldog.ui2.installation

import com.github.mrbean355.admiralbulldog.ui.DotaPath
import com.github.mrbean355.admiralbulldog.ui.getString
import com.github.mrbean355.admiralbulldog.ui2.BoldFont
import com.github.mrbean355.admiralbulldog.ui2.Spacing
import javafx.beans.property.SimpleStringProperty
import tornadofx.Fragment
import tornadofx.action
import tornadofx.button
import tornadofx.chooseDirectory
import tornadofx.fitToParentWidth
import tornadofx.label
import tornadofx.vbox
import java.io.File

class ChooserDotaDirectoryStep : Fragment() {
    private val installationModel by inject<InstallationModel>()
    private var selectedDirectory: File? = null
    private var displayedPath = SimpleStringProperty(getString("install_no_path"))
    private val errorMessage = SimpleStringProperty(null)

    override val root = vbox(spacing = Spacing.MEDIUM) {
        label(getString("install_path_name"))
        label(displayedPath) {
            font = BoldFont()
            isWrapText = true
            fitToParentWidth()
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
