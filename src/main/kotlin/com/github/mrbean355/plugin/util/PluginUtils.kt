package com.github.mrbean355.plugin.util

import kotlin.random.Random

private val random = Random(System.currentTimeMillis())

fun oneInThreeChance(): Boolean {
    return random.nextDouble() <= 0.33
}
