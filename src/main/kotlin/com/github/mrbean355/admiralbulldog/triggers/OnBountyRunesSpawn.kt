package com.github.mrbean355.admiralbulldog.triggers

import com.github.mrbean355.admiralbulldog.game.GameState
import com.github.mrbean355.admiralbulldog.game.MatchState
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import kotlin.math.ceil

/** How often (in seconds) the runes spawn. */
private const val HOW_OFTEN = 5 * 60L

/** Plays a sound shortly before the bounty runes spawn. */
class OnBountyRunesSpawn : SoundTrigger {
    /** Play a sound this many seconds before the bounty runes spawn. */
    private val warningPeriod = ConfigPersistence.getBountyRuneTimer().toLong()
    private var nextPlayTime = UNINITIALISED

    override fun shouldPlay(previous: GameState, current: GameState): Boolean {
        val gameState = current.map!!.game_state
        if (gameState != MatchState.DOTA_GAMERULES_STATE_PRE_GAME && gameState != MatchState.DOTA_GAMERULES_STATE_GAME_IN_PROGRESS) {
            // Don't play during the picking phase.
            return false
        }
        val currentTime = current.map.clock_time
        if (nextPlayTime == UNINITIALISED) {
            nextPlayTime = findNextPlayTime(currentTime)
        }
        if (currentTime >= nextPlayTime) {
            val diff = currentTime - nextPlayTime
            nextPlayTime = findNextPlayTime(currentTime + 10)
            if (diff <= warningPeriod) {
                return true
            }
        }
        return false
    }

    private fun findNextPlayTime(clockTime: Long): Long {
        val iteration = ceil((clockTime + warningPeriod) / HOW_OFTEN.toFloat()).toInt()
        val nextPlayTime = iteration * HOW_OFTEN - warningPeriod
        return nextPlayTime.coerceAtLeast(-warningPeriod)
    }
}