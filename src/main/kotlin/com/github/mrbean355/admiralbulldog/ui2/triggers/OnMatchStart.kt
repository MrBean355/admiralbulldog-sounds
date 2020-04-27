package com.github.mrbean355.admiralbulldog.ui2.triggers

import com.github.mrbean355.admiralbulldog.game.GameState
import com.github.mrbean355.admiralbulldog.game.MatchState
import com.github.mrbean355.admiralbulldog.ui.getString

object OnMatchStart : SoundTrigger {
    override val name = getString("trigger_name_match_start")
    override val description = getString("trigger_desc_match_start")
    private var played = false

    override fun didHappen(previous: GameState, current: GameState): Boolean {
        if (!played && current.map!!.game_state == MatchState.DOTA_GAMERULES_STATE_GAME_IN_PROGRESS && current.map.clock_time < 5) {
            played = true
            return true
        }
        return false
    }

    override fun reset() {
        played = false
    }
}
