package com.github.mrbean355.admiralbulldog.bytes

import com.github.mrbean355.admiralbulldog.game.GameState

/** Plays a sound when the hero gets stunned. */
class OnStunned : RandomSoundByte {
    override val chance = 0.2f

    override fun shouldPlay(previous: GameState, current: GameState): Boolean {
        return !previous.hero!!.stunned && current.hero!!.stunned
    }
}