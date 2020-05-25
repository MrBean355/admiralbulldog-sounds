package com.github.mrbean355.admiralbulldog.events

import com.github.mrbean355.admiralbulldog.game.GameState
import com.github.mrbean355.admiralbulldog.game.MatchState

/** Plays a sound just after the clock hits 0. */
class OnMatchStart : SoundTrigger {
    private var played = false

    override fun shouldPlay(previous: GameState, current: GameState): Boolean {
        if (!played && current.map!!.game_state == MatchState.DOTA_GAMERULES_STATE_GAME_IN_PROGRESS && current.map.clock_time < 5) {
            played = true
            return true
        }
        return false
    }
}