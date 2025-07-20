package com.github.mrbean355.admiralbulldog.styles

import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import javafx.scene.paint.Color
import javafx.scene.text.FontPosture
import javafx.scene.text.FontWeight
import tornadofx.FX
import tornadofx.Stylesheet
import tornadofx.box
import tornadofx.cssclass
import tornadofx.importStylesheet
import tornadofx.px
import tornadofx.removeStylesheet

private const val DARK_THEME = "https://raw.githubusercontent.com/joffrey-bion/javafx-themes/master/css/modena_dark.css"

fun reloadAppStyles() {
    if (ConfigPersistence.isDarkMode()) {
        importStylesheet(DARK_THEME)
        importStylesheet<DarkModeStyles>()
    } else {
        FX.stylesheets.remove(DARK_THEME)
        removeStylesheet<DarkModeStyles>()
    }
}

class AppStyles : Stylesheet() {

    companion object {
        val boldFont by cssclass()
        val italicFont by cssclass()
        val smallFont by cssclass()
        val mediumFont by cssclass()
        val largeFont by cssclass()
        val monospacedFont by cssclass()
        val inlineError by cssclass()
        val iconButton by cssclass()
    }

    init {
        boldFont {
            fontWeight = FontWeight.BOLD
        }
        italicFont {
            fontStyle = FontPosture.ITALIC
        }
        smallFont {
            fontSize = 10.px
        }
        mediumFont {
            fontSize = 14.px
        }
        largeFont {
            fontSize = 18.px
        }
        monospacedFont {
            fontFamily = "Lucida Console"
        }
        inlineError {
            textFill = Color.RED
            fontWeight = FontWeight.BOLD
        }
        iconButton {
            padding = box(vertical = 2.px, horizontal = 4.px)
        }
    }
}