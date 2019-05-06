package com.github.mrbean355.dota2.integration.gamestate

import com.github.mrbean355.dota2.integration.GameState

interface GameStateListener {

    /** Get notified when the game state changes. */
    fun onGameStateUpdated(previousState: GameState, newState: GameState)

    /** Called when the match ID changes. */
    fun reset()
}