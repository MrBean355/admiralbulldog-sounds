package com.github.mrbean355.admiralbulldog

import com.github.mrbean355.admiralbulldog.ui.finalise
import com.github.mrbean355.admiralbulldog.ui.getString
import javafx.application.HostServices
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.CheckBox
import javafx.scene.control.Hyperlink
import javafx.scene.control.Label
import javafx.scene.layout.VBox
import javafx.scene.text.Font
import javafx.stage.Stage

// TODO: Reinstall button (to delete the mod dir)?
class DotaModStage(private val hostServices: HostServices) : Stage() {

    init {
        val viewModel = DotaModViewModel(this)
        val root = VBox(PADDING_MEDIUM).apply {
            padding = Insets(PADDING_MEDIUM)
        }
        root.children += CheckBox(getString("label_enable_mod")).apply {
            selectedProperty().bindBidirectional(viewModel.modEnabled)
        }
        root.children += Label(getString("label_mod_info"))
        root.children += VBox(PADDING_SMALL).apply {
            alignment = Pos.CENTER
            children += Hyperlink(getString("btn_mod_info")).apply {
                setOnAction { hostServices.showDocument(URL_MOD_INFO) }
            }
            children += Label().apply {
                font = Font(TEXT_SIZE_SMALL)
                textProperty().bind(viewModel.modVersion)
            }
        }

        finalise(title = getString("title_mod"), root = root)
        setOnHiding {
            viewModel.onClose()
        }
    }
}
