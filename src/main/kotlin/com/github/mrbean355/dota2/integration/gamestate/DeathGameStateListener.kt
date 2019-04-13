package com.github.mrbean355.dota2.integration.gamestate

import com.github.mrbean355.dota2.integration.GameState
import com.github.mrbean355.dota2.integration.SoundEffect
import com.github.mrbean355.dota2.integration.gamestate.util.oneInThreeChance

/** Random sound effect on death (sometimes). */
class DeathGameStateListener : GameStateListener {

    override fun onGameStateUpdated(previousState: GameState, newState: GameState) {
        if (newState.player!!.deaths > previousState.player!!.deaths && oneInThreeChance()) {
            POSSIBILITIES.random().play()
        }
    }

    private companion object {
        private val POSSIBILITIES = listOf(
                SoundEffect.AAAH,
                SoundEffect.AWOO,
                SoundEffect.BABY_RAGE,
                SoundEffect.CANT_BELIEVE_IT,
                SoundEffect.HELP,
                SoundEffect.NOT_LIKE_THIS,
                SoundEffect.RUN,
                SoundEffect.WTF_MAN)
    }
}