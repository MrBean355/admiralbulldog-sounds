package com.github.mrbean355.admiralbulldog.ui2.home

import com.github.mrbean355.admiralbulldog.ui.getString
import com.github.mrbean355.admiralbulldog.ui2.Spacing
import com.github.mrbean355.admiralbulldog.ui2.Text
import javafx.geometry.Pos.CENTER
import javafx.scene.text.Font
import tornadofx.View
import tornadofx.fitToParentWidth
import tornadofx.imageview
import tornadofx.label
import tornadofx.managedWhen
import tornadofx.paddingAll
import tornadofx.progressbar
import tornadofx.vbox
import tornadofx.visibleWhen

class HomeScreen : View(getString("tab_home")) {
    private val viewModel by inject<HomeViewModel>()

    override val root = vbox(spacing = Spacing.SMALL, alignment = CENTER) {
        paddingAll = Spacing.MEDIUM
        imageview(viewModel.image)
        label(viewModel.label) {
            font = Font(Text.LARGE)
        }
        progressbar {
            fitToParentWidth()
            visibleWhen(viewModel.progressBarVisible)
            managedWhen(visibleProperty())
        }
        label(viewModel.description)
    }
}
