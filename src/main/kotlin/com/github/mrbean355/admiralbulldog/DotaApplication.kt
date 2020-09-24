package com.github.mrbean355.admiralbulldog

import com.github.mrbean355.admiralbulldog.common.BulldogIcon
import com.github.mrbean355.admiralbulldog.exception.UncaughtExceptionHandlerImpl
import com.github.mrbean355.admiralbulldog.home.MainScreen
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import com.github.mrbean355.admiralbulldog.ui.prepareTrayIcon
import javafx.scene.paint.Color.RED
import javafx.scene.text.FontWeight.BOLD
import javafx.stage.Stage
import tornadofx.*
import kotlin.system.exitProcess

class DotaApplication : App(primaryView = MainScreen::class, icon = BulldogIcon(), stylesheet = arrayOf(AppStyles::class)) {

    override fun init() {
        Thread.setDefaultUncaughtExceptionHandler(UncaughtExceptionHandlerImpl(hostServices))
        ConfigPersistence.initialise()
    }

    override fun start(stage: Stage) {
        super.start(stage)
        prepareTrayIcon(stage)
    }

    override fun stop() {
        super.stop()
        exitProcess(0)
    }
}

class AppStyles : Stylesheet() {

    companion object {
        val boldFont by cssclass()
        val smallFont by cssclass()
        val mediumFont by cssclass()
        val largeFont by cssclass()
        val monospacedFont by cssclass()
        val inlineError by cssclass()
        val iconButton by cssclass()
    }

    init {
        boldFont {
            fontWeight = BOLD
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
            textFill = RED
            fontWeight = BOLD
        }
        iconButton {
            padding = box(vertical = 2.px, horizontal = 4.px)
        }
    }
}
