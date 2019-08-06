package com.github.mrbean355.admiralbulldog.bytes

import com.github.mrbean355.admiralbulldog.game.GameState
import java.util.concurrent.TimeUnit

class Periodically : SoundByte {
    private val minQuietTime = TimeUnit.MINUTES.toSeconds(5)
    private val maxQuietTime = TimeUnit.MINUTES.toSeconds(15)
    private var nextPlayClockTime = UNINITIALISED

    override fun shouldPlay(previous: GameState, current: GameState): Boolean {
        if (nextPlayClockTime == UNINITIALISED) {
            nextPlayClockTime = current.map!!.clock_time + random.nextLong(minQuietTime, maxQuietTime)
        } else if (current.map!!.clock_time >= nextPlayClockTime) {
            nextPlayClockTime += random.nextLong(minQuietTime, maxQuietTime)
            return true
        }
        return false
    }
}