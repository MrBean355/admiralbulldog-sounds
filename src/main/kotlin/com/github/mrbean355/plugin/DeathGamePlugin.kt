package com.github.mrbean355.plugin

import com.github.mrbean355.GameState
import com.github.mrbean355.SoundEffect
import com.github.mrbean355.plugin.util.oneInThreeChance

/** Random sound effect on death (sometimes). */
class DeathGamePlugin : GamePlugin {

    override fun onGameStateUpdated(previousState: GameState, newState: GameState) {
        if (newState.player!!.deaths > previousState.player!!.deaths && oneInThreeChance()) {
            listOf(SoundEffect.AAAH,
                    SoundEffect.AWOO,
                    SoundEffect.BABY_RAGE,
                    SoundEffect.CANT_BELIEVE_IT,
                    SoundEffect.HELP,
                    SoundEffect.NOT_LIKE_THIS,
                    SoundEffect.RUN,
                    SoundEffect.WTF_MAN).random().play()
        }
    }
}