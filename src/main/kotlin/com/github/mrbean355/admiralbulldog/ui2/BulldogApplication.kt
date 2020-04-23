package com.github.mrbean355.admiralbulldog.ui2

import tornadofx.App
import tornadofx.launch

class BulldogApplication : App(
        primaryView = MainScreen::class,
        icon = BulldogIcon()
)

fun main() {
    launch<BulldogApplication>()
}
