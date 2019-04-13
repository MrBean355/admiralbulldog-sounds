package com.github.mrbean355.dota2.integration.gamestate

import com.github.mrbean355.dota2.integration.GameState
import com.github.mrbean355.dota2.integration.SoundEffect
import com.github.mrbean355.dota2.integration.gamestate.util.UNINITIALISED
import com.github.mrbean355.dota2.integration.gamestate.util.random

/** Random sound effects as time goes on. */
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

    private companion object {
        private const val MIN = 5 * 60 // seconds
        private const val MAX = 10 * 60 // seconds
        private val POSSIBILITIES = listOf(
                SoundEffect.SLOW_DOWN,
                SoundEffect.ELEGIGGLE,
                SoundEffect.MOVE_YOUR_ASS,
                SoundEffect.PRAISE,
                SoundEffect.SAUSAGE,
                SoundEffect.BERRY_DISGUSTING,
                SoundEffect.NUTS,
                SoundEffect.PEPEGA,
                SoundEffect.ALLIANCE,
                SoundEffect.BABOONS,
                SoundEffect.FEELS_BAD_MAN,
                SoundEffect.PLEBS_ARE_DISGUSTING)
    }
}