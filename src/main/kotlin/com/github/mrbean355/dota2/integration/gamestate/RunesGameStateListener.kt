package com.github.mrbean355.dota2.integration.gamestate

import com.github.mrbean355.dota2.integration.GameState
import com.github.mrbean355.dota2.integration.assets.SoundByte
import com.github.mrbean355.dota2.integration.assets.play
import com.github.mrbean355.dota2.integration.gamestate.util.UNINITIALISED
import kotlin.math.ceil

/** Sound byte shortly before the bounty runes spawn. */
class RunesGameStateListener : GameStateListener {
    private var nextPlayTime = UNINITIALISED

    override fun onGameStateUpdated(previousState: GameState, newState: GameState) {
        val currentTime = newState.map!!.clock_time
        if (currentTime <= 0) {
            return
        }
        if (nextPlayTime == UNINITIALISED) {
            nextPlayTime = findIteration(currentTime)
        }
        if (currentTime >= nextPlayTime) {
            if (currentTime - nextPlayTime <= WARNING_PERIOD) {
                SoundByte.ROONS.play()
            } else {
                println("[WARN] Tried to play ROONS late! currentTime=$currentTime, nextPlayTime=$nextPlayTime")
            }
            nextPlayTime = UNINITIALISED
        }
    }

    override fun reset() {
        nextPlayTime = UNINITIALISED
    }

    private fun findIteration(clockTime: Long): Long {
        val iteration = ceil((clockTime + WARNING_PERIOD) / SOUND_BYTE_PERIOD.toFloat()).toInt()
        val nextPlayTime = iteration * SOUND_BYTE_PERIOD - WARNING_PERIOD
        if (nextPlayTime <= -WARNING_PERIOD) {
            return -WARNING_PERIOD
        }
        return nextPlayTime
    }

    private companion object {
        /** How often runes spawn (seconds) */
        private const val SOUND_BYTE_PERIOD = 5 * 60L // seconds
        /** How many seconds ahead of the spawn time to play the sound. */
        private const val WARNING_PERIOD = 15L // seconds
    }
}