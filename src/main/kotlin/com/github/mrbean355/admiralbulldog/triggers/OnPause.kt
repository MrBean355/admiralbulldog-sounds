package com.github.mrbean355.admiralbulldog.triggers

import com.github.mrbean355.admiralbulldog.game.GameState

class OnPause : SoundTrigger {

    override fun shouldPlay(previous: GameState, current: GameState): Boolean {
        return current.map.isPaused && !previous.map.isPaused
    }
}