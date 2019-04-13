package com.github.mrbean355.dota2.integration.gamestate

import com.github.mrbean355.dota2.integration.GameState
import com.github.mrbean355.dota2.integration.SoundEffect
import com.github.mrbean355.dota2.integration.gamestate.util.TEAM_NONE

/** Sound effect on victory or defeat. */
class MatchEndGameStateListener : GameStateListener {

    override fun onGameStateUpdated(previousState: GameState, newState: GameState) {
        if (newState.map!!.win_team != TEAM_NONE && previousState.map!!.win_team == TEAM_NONE) {
            if (newState.player!!.team_name == newState.map.win_team) {
                SoundEffect.VIVON.play()
            } else {
                SoundEffect.WE_LOST.play()
            }
        }
    }
}