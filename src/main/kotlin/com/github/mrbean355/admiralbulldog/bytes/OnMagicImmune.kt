package com.github.mrbean355.admiralbulldog.bytes

import com.github.mrbean355.admiralbulldog.game.GameState

/** Plays a sound when the hero becomes magic immune. */
class OnMagicImmune : RandomSoundByte {
    override val chance = 0.5f

    override fun shouldPlay(previous: GameState, current: GameState): Boolean {
        return !previous.hero!!.magicimmune && current.hero!!.magicimmune
    }
}