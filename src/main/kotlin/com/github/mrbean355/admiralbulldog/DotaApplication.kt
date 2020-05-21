package com.github.mrbean355.admiralbulldog

import com.github.mrbean355.admiralbulldog.common.BulldogIcon
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import tornadofx.App
import kotlin.system.exitProcess

class DotaApplication : App(primaryView = MainScreen::class, icon = BulldogIcon()) {

    override fun init() {
        Thread.setDefaultUncaughtExceptionHandler(UncaughtExceptionHandlerImpl(hostServices))
        ConfigPersistence.initialise()
    }

    override fun stop() {
        super.stop()
        exitProcess(0)
    }
}