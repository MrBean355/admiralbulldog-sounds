package com.github.mrbean355.dota2.integration.gamestate

import com.github.mrbean355.dota2.integration.GameState
import com.github.mrbean355.dota2.integration.SoundByte

class RespawnGameStateListener : GameStateListener {

    override fun onGameStateUpdated(previousState: GameState, newState: GameState) {
        if (!previousState.hero!!.alive && newState.hero!!.alive) {
            POSSIBILITIES.random().play()
        }
    }

    private companion object {
        private val POSSIBILITIES = listOf(
                SoundByte.MOTHERCOMES,
                SoundByte.RONNIE,
                SoundByte.UGANDA,
                SoundByte.WARRIA,
                SoundByte.YEEHAW
        )
    }
}