package com.github.mrbean355.admiralbulldog.events

import com.github.mrbean355.admiralbulldog.game.GameState

/** Plays a sound when the hero becomes magic immune. */
class OnMagicImmune : RandomSoundEvent {
    override val chance = 0.5f

    override fun shouldPlay(previous: GameState, current: GameState): Boolean {
        return !previous.hero!!.magicimmune && current.hero!!.magicimmune
    }
}