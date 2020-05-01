package com.github.mrbean355.admiralbulldog.ui2

import com.github.mrbean355.admiralbulldog.gsi.DotaClient
import tornadofx.App
import tornadofx.launch
import kotlin.system.exitProcess

class BulldogApplication : App(
        primaryView = MainScreen::class,
        icon = BulldogIcon()
) {
    private val dotaClient = DotaClient()

    override fun stop() {
        dotaClient.stop()
        super.stop()
        exitProcess(0)
    }
}

fun main() {
    launch<BulldogApplication>()
}
