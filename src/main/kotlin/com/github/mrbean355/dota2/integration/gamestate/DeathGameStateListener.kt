package com.github.mrbean355.dota2.integration.gamestate

import com.github.mrbean355.dota2.integration.GameState
import com.github.mrbean355.dota2.integration.SoundByte
import com.github.mrbean355.dota2.integration.gamestate.util.oneInThreeChance

/** Random sound byte on death (sometimes). */
class DeathGameStateListener : GameStateListener {

    override fun onGameStateUpdated(previousState: GameState, newState: GameState) {
        if (newState.player!!.deaths > previousState.player!!.deaths && oneInThreeChance()) {
            POSSIBILITIES.random().play()
        }
    }

    private companion object {
        private val POSSIBILITIES = listOf(
                SoundByte.AHAHA4HEAD,
                SoundByte.AWOO,
                SoundByte.BABYRAGE,
                SoundByte.BERRYDISGUSTING,
                SoundByte.BULLDOGAAAH,
                SoundByte.FOURHEAD,
                SoundByte.GODDAMNPEPEGA,
                SoundByte.HELP,
                SoundByte.ICANTBELIEVEIT,
                SoundByte.NOTLIKETHIS,
                SoundByte.OHNONONO,
                SoundByte.RUN,
                SoundByte.WASHEDUP,
                SoundByte.WTFMAN,
                SoundByte.YIKES)
    }
}