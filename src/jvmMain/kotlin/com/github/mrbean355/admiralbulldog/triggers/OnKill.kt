package com.github.mrbean355.admiralbulldog.triggers

import com.github.mrbean355.admiralbulldog.game.GameState

class OnKill : SoundTrigger {

    override fun shouldPlay(previous: GameState, current: GameState): Boolean {
        previous.player ?: return false
        current.player ?: return false

        return current.player.kills > previous.player.kills
    }
}