package com.github.mrbean355.dota2.integration.bytes

import com.github.mrbean355.dota2.integration.GameState

class OnMatchStart : SoundByte {

    override fun shouldPlay(previous: GameState, current: GameState): Boolean {
        return previous.map?.matchid == null && current.map?.matchid != null
    }
}