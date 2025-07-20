package com.github.mrbean355.admiralbulldog.triggers

import com.github.mrbean355.admiralbulldog.game.GameState
import com.github.mrbean355.dota2.map.Team

class OnVictory : SoundTrigger {

    override fun shouldPlay(previous: GameState, current: GameState): Boolean {
        current.player ?: return false

        return current.map.winningTeam != Team.None && previous.map.winningTeam == Team.None
                && current.player.team == current.map.winningTeam
    }
}