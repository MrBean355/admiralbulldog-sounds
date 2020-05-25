package com.github.mrbean355.admiralbulldog.triggers

import com.github.mrbean355.admiralbulldog.game.GameState

class OnKill : SoundTrigger {

    override fun shouldPlay(previous: GameState, current: GameState): Boolean {
        return current.player!!.kills > previous.player!!.kills
    }
}