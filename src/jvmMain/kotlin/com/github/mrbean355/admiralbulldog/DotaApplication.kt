package com.github.mrbean355.admiralbulldog

import com.github.mrbean355.admiralbulldog.common.BulldogIcon
import com.github.mrbean355.admiralbulldog.exception.UncaughtExceptionHandlerImpl
import com.github.mrbean355.admiralbulldog.home.openMainScreen
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import com.github.mrbean355.admiralbulldog.styles.AppStyles
import com.github.mrbean355.admiralbulldog.styles.reloadAppStyles
import javafx.application.Platform
import javafx.stage.Stage
import tornadofx.App
import kotlin.system.exitProcess

class DotaApplication : App(icon = BulldogIcon(), stylesheet = arrayOf(AppStyles::class)) {
    companion object {
        lateinit var hostServices: javafx.application.HostServices
    }

    override fun init() {
        DotaApplication.hostServices = hostServices
        Thread.setDefaultUncaughtExceptionHandler(UncaughtExceptionHandlerImpl(hostServices))
        ConfigPersistence.initialise()
        reloadAppStyles()
    }

    override fun start(stage: Stage) {
        super.start(stage)
        stage.hide()
        Platform.setImplicitExit(!ConfigPersistence.isMinimizeToTray())
        openMainScreen()
    }

    override fun stop() {
        super.stop()
        exitProcess(0)
    }
}
