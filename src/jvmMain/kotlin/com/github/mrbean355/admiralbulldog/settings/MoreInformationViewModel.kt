package com.github.mrbean355.admiralbulldog.settings

import com.github.mrbean355.admiralbulldog.arch.ComposeViewModel
import java.awt.Desktop
import java.net.URI

class MoreInformationViewModel : ComposeViewModel() {

    fun openUrl(url: String) {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(URI(url))
        }
    }
}
