package com.github.mrbean355.plugin

import com.github.mrbean355.GameState
import com.github.mrbean355.SoundEffect

/** Sound effect on victory or defeat. */
class MatchEndGamePlugin : GamePlugin {

    override fun onGameStateUpdated(previousState: GameState, newState: GameState) {
        if (newState.map!!.win_team != TEAM_NONE && previousState.map!!.win_team == TEAM_NONE) {
            if (newState.player!!.team_name == newState.map.win_team) {
                SoundEffect.VIVON.play()
            } else {
                SoundEffect.WE_LOST.play()
            }
        }
    }

    private companion object {
        private const val TEAM_NONE = "none"
    }
}