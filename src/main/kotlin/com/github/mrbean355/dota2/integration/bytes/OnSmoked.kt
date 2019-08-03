package com.github.mrbean355.dota2.integration.bytes

import com.github.mrbean355.dota2.integration.GameState

class OnSmoked : SoundByte {

    override fun shouldPlay(previous: GameState, current: GameState): Boolean {
        return !previous.hero!!.smoked && current.hero!!.smoked
    }
}