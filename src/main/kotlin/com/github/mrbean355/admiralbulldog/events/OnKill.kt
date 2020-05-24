package com.github.mrbean355.admiralbulldog.events

import com.github.mrbean355.admiralbulldog.game.GameState

class OnKill : SoundEvent {

    override fun shouldPlay(previous: GameState, current: GameState): Boolean {
        return current.player!!.kills > previous.player!!.kills
    }
}