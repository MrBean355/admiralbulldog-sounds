package com.github.mrbean355.plugin

import com.github.mrbean355.GameState
import com.github.mrbean355.SoundEffect

/** Sound effect shortly before the bounty runes spawn. */
class RunesGamePlugin : GamePlugin {
    private var coolDownUntil = -1L

    override fun onGameStateUpdated(previousState: GameState, newState: GameState) {
        val currentTime = System.currentTimeMillis()
        /* We get updates more than once a second, which causes the sound to play more than expected. */
        if (newState.map!!.clock_time <= 0 || currentTime < coolDownUntil) {
            return
        }
        val secondsLeft = (newState.map.clock_time + WARNING_PERIOD) % SOUND_EFFECT_PERIOD
        if (secondsLeft == 0L) {
            coolDownUntil = currentTime + COOL_DOWN_PERIOD
            SoundEffect.ROONS.play()
        }
    }

    private companion object {
        /** How often runes spawn (seconds) */
        private const val SOUND_EFFECT_PERIOD = 5 * 60
        /** How many seconds ahead of the spawn time to play the sound. */
        private const val WARNING_PERIOD = 15
        /** Duration (milliseconds) to wait before playing the sound again. */
        private const val COOL_DOWN_PERIOD = 4 * 60 * 1000
    }
}