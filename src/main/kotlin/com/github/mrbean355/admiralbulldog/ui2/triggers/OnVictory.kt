package com.github.mrbean355.admiralbulldog.ui2.triggers

import com.github.mrbean355.admiralbulldog.events.TEAM_NONE
import com.github.mrbean355.admiralbulldog.game.GameState
import com.github.mrbean355.admiralbulldog.ui.getString

object OnVictory : SoundTrigger {
    override val name = getString("trigger_name_victory")
    override val description = getString("trigger_desc_victory")

    override fun didHappen(previous: GameState, current: GameState): Boolean {
        return current.map!!.win_team != TEAM_NONE && previous.map!!.win_team == TEAM_NONE
                && current.player!!.team_name == current.map.win_team
    }
}
