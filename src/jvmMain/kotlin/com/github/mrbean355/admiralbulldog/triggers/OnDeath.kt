package com.github.mrbean355.admiralbulldog.triggers

import com.github.mrbean355.admiralbulldog.game.GameState

class OnDeath : SoundTrigger {

    override fun shouldPlay(previous: GameState, current: GameState): Boolean {
        previous.player ?: return false
        current.player ?: return false

        return current.player.deaths > previous.player.deaths
    }
}