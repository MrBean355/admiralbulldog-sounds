package com.github.mrbean355.admiralbulldog.styles

import javafx.scene.paint.Color
import tornadofx.Stylesheet

class DarkModeStyles : Stylesheet() {

    companion object {
        val WIZARD_HEADER: Color = Color.rgb(75, 75, 75)
    }

    init {
        hyperlink {
            textFill = Color.CORNFLOWERBLUE
        }
    }
}