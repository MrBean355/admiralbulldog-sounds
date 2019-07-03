package com.github.mrbean355.dota2.integration.gamestate

import com.github.mrbean355.dota2.integration.GameState
import com.github.mrbean355.dota2.integration.assets.SoundByte
import com.github.mrbean355.dota2.integration.assets.play

/** Sound byte on Smoke of Deceit used. */
class SmokeOfDeceitGameStateListener : GameStateListener {

    override fun onGameStateUpdated(previousState: GameState, newState: GameState) {
        if (newState.hero!!.smoked && !previousState.hero!!.smoked) {
            SoundByte.WEED.play()
        }
    }

    override fun reset() {}
}