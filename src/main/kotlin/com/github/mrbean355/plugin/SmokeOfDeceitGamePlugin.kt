package com.github.mrbean355.plugin

import com.github.mrbean355.GameState
import com.github.mrbean355.SoundEffect

/** Sound effect on Smoke of Deceit used. */
class SmokeOfDeceitGamePlugin : GamePlugin {

    override fun onGameStateUpdated(previousState: GameState, newState: GameState) {
        if (newState.hero!!.smoked && !previousState.hero!!.smoked) {
            SoundEffect.SMOKE_WEED.play()
        }
    }
}