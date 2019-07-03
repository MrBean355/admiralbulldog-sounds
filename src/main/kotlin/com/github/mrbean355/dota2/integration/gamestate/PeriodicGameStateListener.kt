package com.github.mrbean355.dota2.integration.gamestate

import com.github.mrbean355.dota2.integration.GameState
import com.github.mrbean355.dota2.integration.assets.SoundByte
import com.github.mrbean355.dota2.integration.assets.play
import com.github.mrbean355.dota2.integration.gamestate.util.UNINITIALISED
import com.github.mrbean355.dota2.integration.gamestate.util.random

/** Random sound bytes as time goes on. */
class PeriodicGameStateListener : GameStateListener {
    private var nextPlayClockTime = UNINITIALISED

    override fun onGameStateUpdated(previousState: GameState, newState: GameState) {
        if (nextPlayClockTime == UNINITIALISED) {
            nextPlayClockTime = newState.map!!.clock_time + random.nextInt(MIN, MAX)
        } else if (newState.map!!.clock_time >= nextPlayClockTime) {
            nextPlayClockTime += random.nextInt(MIN, MAX)
            POSSIBILITIES.random().play()
        }
    }

    override fun reset() {
        nextPlayClockTime = UNINITIALISED
    }

    private companion object {
        private const val MIN = 5 * 60 // seconds
        private const val MAX = 10 * 60 // seconds
        private val POSSIBILITIES = listOf(
                SoundByte.ADMIRALC,
                SoundByte.ALLIANCE,
                SoundByte.FEELSGOODMAN,
                SoundByte.MOTHERCOMES,
                SoundByte.MOVEYOURASS,
                SoundByte.NUTS,
                SoundByte.PERMABAN,
                SoundByte.PLEBSAREDISGUSTING,
                SoundByte.PRAISE,
                SoundByte.RONNIE,
                SoundByte.SAUSAGE,
                SoundByte.SLOWDOWN)
    }
}