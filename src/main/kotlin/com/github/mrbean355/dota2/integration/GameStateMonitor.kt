package com.github.mrbean355.dota2.integration

import com.github.mrbean355.dota2.integration.gamestate.*

/** Notify registered plugins when the game state changes. */
class GameStateMonitor {
    private var previousState: GameState? = null
    private val gameStateListeners: List<GameStateListener> = listOf(
            DeathGameStateListener(),
            HealGameStateListener(),
            KillGameStateListener(),
            MatchEndGameStateListener(),
            SmokeOfDeceitGameStateListener(),
            RunesGameStateListener(),
            MidasGameStateListener(),
            PeriodicGameStateListener()
    )

    init {
        // Preload
        SoundByte.BULLDOGHANDSUP.play()
    }

    fun onUpdate(newState: GameState) {
        val previousState = previousState
        if (previousState != null && previousState.hasValidProperties() && newState.hasValidProperties()) {
            gameStateListeners.forEach {
                it.onGameStateUpdated(previousState, newState)
            }
        }
        this.previousState = newState
    }
}
