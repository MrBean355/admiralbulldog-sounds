package com.github.mrbean355.dota2.integration.gamestate

import com.github.mrbean355.dota2.integration.GameState
import com.github.mrbean355.dota2.integration.assets.SoundByte
import com.github.mrbean355.dota2.integration.assets.play
import com.github.mrbean355.dota2.integration.gamestate.util.oneInThreeChance

/** Random sound byte on death (sometimes). */
class DeathGameStateListener : GameStateListener {

    override fun onGameStateUpdated(previousState: GameState, newState: GameState) {
        if (newState.player!!.deaths > previousState.player!!.deaths && oneInThreeChance()) {
            POSSIBILITIES.random().play()
        }
    }

    override fun reset() {}

    private companion object {
        private val POSSIBILITIES = listOf(
                SoundByte.AWOO,
                SoundByte.BABYRAGE,
                SoundByte.BANTER,
                SoundByte.BULLDOGAAAH,
                SoundByte.BULLDOGAAAH2,
                SoundByte.FUCKRIGHTOFF,
                SoundByte.GODDAMNPEPEGA,
                SoundByte.HELP,
                SoundByte.ICANTBELIEVEIT,
                SoundByte.ITHURTS,
                SoundByte.LOOL4HEAD,
                SoundByte.NOTLIKETHIS,
                SoundByte.OHNO,
                SoundByte.OHNONO,
                SoundByte.OHNONONO,
                SoundByte.OHNONONO07,
                SoundByte.PERMASMASH,
                SoundByte.RUN,
                SoundByte.TP,
                SoundByte.WTFMAN)
    }
}