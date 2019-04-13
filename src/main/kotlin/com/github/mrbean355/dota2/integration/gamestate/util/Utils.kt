package com.github.mrbean355.dota2.integration.gamestate.util

import kotlin.random.Random

const val UNINITIALISED = -1L
const val TEAM_NONE = "none"
val random = Random(System.currentTimeMillis())

fun oneInThreeChance(): Boolean {
    return random.nextDouble() <= 0.33
}
