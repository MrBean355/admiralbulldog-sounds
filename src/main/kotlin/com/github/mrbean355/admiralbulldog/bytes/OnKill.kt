package com.github.mrbean355.admiralbulldog.bytes

import com.github.mrbean355.admiralbulldog.game.GameState

class OnKill : RandomSoundByte {
    override val chance = 0.33f

    override fun shouldPlay(previous: GameState, current: GameState): Boolean {
        return current.player!!.kills > previous.player!!.kills
    }
}