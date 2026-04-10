package com.github.mrbean355.admiralbulldog.triggers

import com.github.mrbean355.admiralbulldog.game.GameState

class OnSmoked : SoundTrigger {

    override fun shouldPlay(previous: GameState, current: GameState): Boolean {
        previous.hero ?: return false
        current.hero ?: return false

        return !previous.hero.isSmoked && current.hero.isSmoked
    }
}