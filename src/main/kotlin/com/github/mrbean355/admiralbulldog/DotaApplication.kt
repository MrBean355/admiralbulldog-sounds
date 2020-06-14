package com.github.mrbean355.admiralbulldog

import com.github.mrbean355.admiralbulldog.common.BulldogIcon
import com.github.mrbean355.admiralbulldog.home.MainScreen
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import com.github.mrbean355.admiralbulldog.ui.prepareTrayIcon
import javafx.scene.paint.Color.RED
import javafx.scene.text.FontWeight.BOLD
import javafx.stage.Stage
import tornadofx.App
import tornadofx.Stylesheet
import tornadofx.box
import tornadofx.cssclass
import tornadofx.px
import kotlin.system.exitProcess

class DotaApplication : App(primaryView = MainScreen::class, icon = BulldogIcon(), stylesheet = *arrayOf(AppStyles::class)) {

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
        val inlineError by cssclass()
        val iconButton by cssclass()
    }

    init {
        inlineError {
            textFill = RED
            fontWeight = BOLD
        }
        iconButton {
            padding = box(vertical = 2.px, horizontal = 4.px)
        }
    }
}
