package com.github.mrbean355.dota2.integration.gamestate

import com.github.mrbean355.dota2.integration.GameState
import com.github.mrbean355.dota2.integration.SoundByte
import com.github.mrbean355.dota2.integration.gamestate.util.UNINITIALISED

/** Sound byte shortly before the bounty runes spawn. */
class RunesGameStateListener : GameStateListener {
    private var cooldownUntil = UNINITIALISED

    override fun onGameStateUpdated(previousState: GameState, newState: GameState) {
        /* We get updates more than once a second, which causes the sound to play more than expected. */
        if (newState.map!!.clock_time <= 0 || newState.map.clock_time < cooldownUntil) {
            return
        }
        val secondsLeft = (newState.map.clock_time + WARNING_PERIOD) % SOUND_BYTE_PERIOD
        if (secondsLeft == 0L) {
            cooldownUntil = newState.map.clock_time + COOL_DOWN_PERIOD
            SoundByte.ROONS.play()
        }
    }

    private companion object {
        /** How often runes spawn (seconds) */
        private const val SOUND_BYTE_PERIOD = 5 * 60 // seconds
        /** How many seconds ahead of the spawn time to play the sound. */
        private const val WARNING_PERIOD = 15 // seconds
        /** Duration (milliseconds) to wait before playing the sound again. */
        private const val COOL_DOWN_PERIOD = 4 * 60 // seconds
    }
}