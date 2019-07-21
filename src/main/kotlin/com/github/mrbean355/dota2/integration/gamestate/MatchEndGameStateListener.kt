package com.github.mrbean355.dota2.integration.gamestate

import com.github.mrbean355.dota2.integration.GameState
import com.github.mrbean355.dota2.integration.assets.SoundByte
import com.github.mrbean355.dota2.integration.assets.play
import com.github.mrbean355.dota2.integration.gamestate.util.TEAM_NONE

/** Sound byte on victory or defeat. */
class MatchEndGameStateListener : GameStateListener {

    override fun onGameStateUpdated(previousState: GameState, newState: GameState) {
        if (newState.map!!.win_team != TEAM_NONE && previousState.map!!.win_team == TEAM_NONE) {
            if (newState.player!!.team_name == newState.map.win_team) {
                WIN_POSSIBILITIES.random().play()
            } else {
                SoundByte.WEFUCKINGLOST.play()
            }
        }
    }

    override fun reset() {}

    private companion object {
        private val WIN_POSSIBILITIES = listOf(
                SoundByte.OMEGAEZ,
                SoundByte.VIVON)
    }
}