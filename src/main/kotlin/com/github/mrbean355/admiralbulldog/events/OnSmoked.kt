package com.github.mrbean355.admiralbulldog.events

import com.github.mrbean355.admiralbulldog.game.GameState

class OnSmoked : SoundTrigger {

    override fun shouldPlay(previous: GameState, current: GameState): Boolean {
        return !previous.hero!!.smoked && current.hero!!.smoked
    }
}