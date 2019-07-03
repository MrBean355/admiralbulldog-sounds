package com.github.mrbean355.dota2.integration.gamestate

import com.github.mrbean355.dota2.integration.GameState
import com.github.mrbean355.dota2.integration.assets.SoundByte
import com.github.mrbean355.dota2.integration.assets.SpecialSoundByte
import com.github.mrbean355.dota2.integration.assets.play
import com.github.mrbean355.dota2.integration.gamestate.util.TEAM_NONE

/** Sound byte on victory or defeat. */
class MatchEndGameStateListener : GameStateListener {

    override fun onGameStateUpdated(previousState: GameState, newState: GameState) {
        if (newState.map!!.win_team != TEAM_NONE && previousState.map!!.win_team == TEAM_NONE) {
            if (newState.player!!.team_name == newState.map.win_team) {
                SoundByte.VIVON.play()
            } else {
                SpecialSoundByte.WE_LOST.play()
            }
        }
    }

    override fun reset() {}
}