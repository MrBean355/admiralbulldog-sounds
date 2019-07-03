package com.github.mrbean355.dota2.integration

import com.github.mrbean355.dota2.integration.assets.SoundByte
import com.github.mrbean355.dota2.integration.assets.play
import com.github.mrbean355.dota2.integration.gamestate.*

/** Notify registered plugins when the game state changes. */
class GameStateMonitor {
    private var previousState: GameState? = null
    private val gameStateListeners: List<GameStateListener> = listOf(
            DeathGameStateListener(),
            HealGameStateListener(),
            KillGameStateListener(),
            MatchEndGameStateListener(),
            MidasGameStateListener(),
            PeriodicGameStateListener(),
            RunesGameStateListener(),
            SmokeOfDeceitGameStateListener()
    )

    init {
        // Preload
        SoundByte.BULLDOGHANDSUP.play()
    }

    fun onUpdate(newState: GameState) {
        val previousState = previousState
        if (previousState != null) {
            val prevMatch = previousState.map?.matchid
            val curMatch = newState.map?.matchid
            if (prevMatch != curMatch) {
                if (curMatch == null) {
                    println("Left match $prevMatch")
                } else {
                    println("Entered match $curMatch")
                }
                gameStateListeners.forEach {
                    it.reset()
                }
            }
        }
        if (previousState != null && previousState.hasValidProperties() && newState.hasValidProperties()) {
            gameStateListeners.forEach {
                it.onGameStateUpdated(previousState, newState)
            }
        }
        this.previousState = newState
    }
}
