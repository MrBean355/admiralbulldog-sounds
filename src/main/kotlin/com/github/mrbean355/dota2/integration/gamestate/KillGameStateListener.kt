package com.github.mrbean355.dota2.integration.gamestate

import com.github.mrbean355.dota2.integration.GameState
import com.github.mrbean355.dota2.integration.SoundEffect
import com.github.mrbean355.dota2.integration.gamestate.util.oneInThreeChance

/** Random sound effect on kills (sometimes). */
class KillGameStateListener : GameStateListener {

    override fun onGameStateUpdated(previousState: GameState, newState: GameState) {
        if (newState.player!!.kills > previousState.player!!.kills && oneInThreeChance()) {
            POSSIBILITIES.random().play()
        }
    }

    private companion object {
        private val POSSIBILITIES = listOf(
                SoundEffect.BLBLBL,
                SoundEffect.BULLDOG4HEAD,
                SoundEffect.BULLDOGHANDSUP,
                SoundEffect.DUMBSHITPLEB,
                SoundEffect.IMCOMING,
                SoundEffect.LULDOG,
                SoundEffect.OHMYGOD,
                SoundEffect.OMEGAEZ,
                SoundEffect.THATSPOWER,
                SoundEffect.UNLIMITEDPOWER)
    }
}