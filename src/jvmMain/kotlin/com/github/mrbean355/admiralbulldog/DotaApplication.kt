package com.github.mrbean355.admiralbulldog

import com.github.mrbean355.admiralbulldog.exception.UncaughtExceptionHandlerImpl
import com.github.mrbean355.admiralbulldog.home.openMainScreen
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import javafx.application.Platform
import javafx.stage.Stage
import kotlin.system.exitProcess

class DotaApplication : javafx.application.Application() {
    companion object {
        lateinit var hostServices: javafx.application.HostServices
    }

    override fun init() {
        DotaApplication.hostServices = hostServices
        Thread.setDefaultUncaughtExceptionHandler(UncaughtExceptionHandlerImpl(hostServices))
        ConfigPersistence.initialise()
    }

    override fun start(stage: Stage) {
        stage.hide()
        Platform.setImplicitExit(!ConfigPersistence.isMinimizeToTray())
        openMainScreen()
    }

    override fun stop() {
        exitProcess(0)
    }
}
