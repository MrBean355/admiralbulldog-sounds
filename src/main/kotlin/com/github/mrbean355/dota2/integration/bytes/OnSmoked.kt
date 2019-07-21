package com.github.mrbean355.dota2.integration.bytes

import com.github.mrbean355.dota2.integration.GameState
import com.github.mrbean355.dota2.integration.assets.SoundFile

class OnSmoked : SoundByte {
    override val choices = listOf(SoundFile.WEED)

    override fun shouldPlay(previous: GameState, current: GameState): Boolean {
        return !previous.hero!!.smoked && current.hero!!.smoked
    }
}