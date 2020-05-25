package com.github.mrbean355.admiralbulldog.events

import com.github.mrbean355.admiralbulldog.game.GameState

class OnVictory : SoundTrigger {

    override fun shouldPlay(previous: GameState, current: GameState): Boolean {
        return current.map!!.win_team != TEAM_NONE && previous.map!!.win_team == TEAM_NONE
                && current.player!!.team_name == current.map.win_team
    }
}