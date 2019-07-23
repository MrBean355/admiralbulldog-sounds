package com.github.mrbean355.dota2.integration.bytes

import com.github.mrbean355.dota2.integration.GameState
import com.github.mrbean355.dota2.integration.assets.SoundFile

class OnMatchStart : SoundByte {
    override val choices = listOf(SoundFile.TEAMPEPEGA)

    override fun shouldPlay(previous: GameState, current: GameState): Boolean {
        return previous.map?.matchid == null && current.map?.matchid != null
    }
}