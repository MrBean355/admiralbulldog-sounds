package com.github.mrbean355.admiralbulldog.triggers

import com.github.mrbean355.admiralbulldog.game.GameState
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import kotlin.random.Random

class Periodically : SoundTrigger {
    private var nextPlayClockTime = UNINITIALISED

    override fun shouldPlay(previous: GameState, current: GameState): Boolean {
        if (nextPlayClockTime == UNINITIALISED) {
            nextPlayClockTime = current.map!!.clock_time + randomiseDelay()
        } else if (current.map!!.clock_time >= nextPlayClockTime) {
            nextPlayClockTime += randomiseDelay()
            return true
        }
        return false
    }

    private fun randomiseDelay(): Int {
        // Cast to double so we can randomise a non-whole amount of minutes.
        val minQuietTime = ConfigPersistence.getMinPeriod().toDouble()
        val maxQuietTime = ConfigPersistence.getMaxPeriod().toDouble()
        return if (minQuietTime != maxQuietTime) {
            Random.nextDouble(minQuietTime, maxQuietTime)
        } else {
            minQuietTime
        }.toInt() * 60
    }
}