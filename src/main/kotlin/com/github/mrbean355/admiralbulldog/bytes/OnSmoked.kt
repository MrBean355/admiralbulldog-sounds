package com.github.mrbean355.admiralbulldog.bytes

import com.github.mrbean355.admiralbulldog.game.GameState

class OnSmoked : SoundByte {

    override fun shouldPlay(previous: GameState, current: GameState): Boolean {
        return !previous.hero!!.smoked && current.hero!!.smoked
    }
}