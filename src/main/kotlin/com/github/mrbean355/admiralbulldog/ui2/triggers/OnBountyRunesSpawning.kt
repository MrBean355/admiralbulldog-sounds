package com.github.mrbean355.admiralbulldog.ui2.triggers

import com.github.mrbean355.admiralbulldog.events.UNINITIALISED
import com.github.mrbean355.admiralbulldog.game.GameState
import com.github.mrbean355.admiralbulldog.game.MatchState
import com.github.mrbean355.admiralbulldog.ui.getString
import kotlin.math.ceil

/** How often (in seconds) the runes spawn. */
private const val HOW_OFTEN = 5 * 60L

/** Play a sound this many seconds before the bounty runes spawn. */
private const val WARNING_PERIOD = 15L

object OnBountyRunesSpawning : SoundTrigger {
    override val name = getString("trigger_name_bounty_runes")
    override val description = getString("trigger_desc_bounty_runes")
    private var nextPlayTime = UNINITIALISED

    override fun didHappen(previous: GameState, current: GameState): Boolean {
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
            if (diff <= WARNING_PERIOD) {
                return true
            }
        }
        return false
    }

    override fun reset() {
        nextPlayTime = UNINITIALISED
    }

    private fun findNextPlayTime(clockTime: Long): Long {
        val iteration = ceil((clockTime + WARNING_PERIOD) / HOW_OFTEN.toFloat()).toInt()
        val nextPlayTime = iteration * HOW_OFTEN - WARNING_PERIOD
        return nextPlayTime.coerceAtLeast(-WARNING_PERIOD)
    }
}
