package com.github.mrbean355.admiralbulldog.ui2

import com.github.mrbean355.admiralbulldog.gsi.DotaClient
import tornadofx.App
import tornadofx.launch

class BulldogApplication : App(
        primaryView = MainScreen::class,
        icon = BulldogIcon()
) {
    private val dotaClient = DotaClient()

    override fun stop() {
        dotaClient.stop()
        super.stop()
    }
}

fun main() {
    launch<BulldogApplication>()
}
