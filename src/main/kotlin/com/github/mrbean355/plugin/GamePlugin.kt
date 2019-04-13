package com.github.mrbean355.plugin

import com.github.mrbean355.GameState

interface GamePlugin {

    /** Get notified when the game state changes. */
    fun onGameStateUpdated(previousState: GameState, newState: GameState)
}