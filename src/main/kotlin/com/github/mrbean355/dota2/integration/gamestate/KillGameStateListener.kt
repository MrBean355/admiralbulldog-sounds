package com.github.mrbean355.dota2.integration.gamestate

import com.github.mrbean355.dota2.integration.GameState
import com.github.mrbean355.dota2.integration.assets.SoundByte
import com.github.mrbean355.dota2.integration.assets.play
import com.github.mrbean355.dota2.integration.gamestate.util.oneInThreeChance

/** Random sound byte on kills (sometimes). */
class KillGameStateListener : GameStateListener {

    override fun onGameStateUpdated(previousState: GameState, newState: GameState) {
        if (newState.player!!.kills > previousState.player!!.kills && oneInThreeChance()) {
            POSSIBILITIES.random().play()
        }
    }

    override fun reset() {}

    private companion object {
        private val POSSIBILITIES = listOf(
                SoundByte.`12345DEAD`,
                SoundByte.AREDOINGIT,
                SoundByte.BERRYDISGUSTING,
                SoundByte.BLBLBL,
                SoundByte.BLBLBL2,
                SoundByte.BULLDOG4HEAD,
                SoundByte.BULLDOGHANDSUP,
                SoundByte.DUMBSHITPLEB,
                SoundByte.ELEGIGGLE,
                SoundByte.FEELSBADMAN,
                SoundByte.GODDAMNBABOONS,
                SoundByte.IMCOMING,
                SoundByte.KREYGASM,
                SoundByte.NAMASTE,
                SoundByte.LULDOG,
                SoundByte.LULWJEBAITED,
                SoundByte.MONKAGIGA,
                SoundByte.OHMYGOD,
                SoundByte.OMEGAEZ,
                SoundByte.SEEYA,
                SoundByte.SKADOOSH,
                SoundByte.THATSPOWER,
                SoundByte.UNLIMITEDPOWER,
                SoundByte.WTFF)
    }
}