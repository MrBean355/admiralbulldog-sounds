package com.github.mrbean355.admiralbulldog.events

import com.github.mrbean355.admiralbulldog.game.GameState

class OnRespawn : SoundEvent {

    override fun shouldPlay(previous: GameState, current: GameState): Boolean {
        return previous.hero!!.respawn_seconds > 0 && current.hero!!.respawn_seconds == 0
    }
}