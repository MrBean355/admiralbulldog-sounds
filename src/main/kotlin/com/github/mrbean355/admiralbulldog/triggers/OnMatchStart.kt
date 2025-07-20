package com.github.mrbean355.admiralbulldog.triggers

import com.github.mrbean355.admiralbulldog.game.GameState
import com.github.mrbean355.dota2.map.MatchState

/** Plays a sound just after the clock hits 0. */
class OnMatchStart : SoundTrigger {
    private var played = false

    override fun shouldPlay(previous: GameState, current: GameState): Boolean {
        if (!played && current.map.matchState == MatchState.GameInProgress && current.map.clockTime < 5) {
            played = true
            return true
        }
        return false
    }
}