package com.github.mrbean355.dota2.integration.gamestate

import com.github.mrbean355.dota2.integration.GameState
import com.github.mrbean355.dota2.integration.SoundByte
import com.github.mrbean355.dota2.integration.gamestate.util.UNINITIALISED
import com.github.mrbean355.dota2.integration.gamestate.util.random

/** Random sound bytes as time goes on. */
class PeriodicGameStateListener : GameStateListener {
    private var nextPlayClockTime = UNINITIALISED

    override fun onGameStateUpdated(previousState: GameState, newState: GameState) {
        if (nextPlayClockTime == UNINITIALISED) {
            nextPlayClockTime = newState.map!!.clock_time + random.nextInt(MIN, MAX)
        } else if (newState.map!!.clock_time >= nextPlayClockTime) {
            nextPlayClockTime += random.nextInt(MIN, MAX)
            POSSIBILITIES.random().play()
        }
    }

    private companion object {
        private const val MIN = 5 * 60 // seconds
        private const val MAX = 10 * 60 // seconds
        private val POSSIBILITIES = listOf(
                SoundByte.ADMIRALC,
                SoundByte.AHAHA4HEAD,
                SoundByte.ALLIANCE,
                SoundByte.BADGRILL,
                SoundByte.BRUH,
                SoundByte.CATERPILLAR,
                SoundByte.CHEFS4,
                SoundByte.CLICKFORFREEPOINTS,
                SoundByte.COLLATERALDAMAGE,
                SoundByte.DAMNSON,
                SoundByte.DENMARK,
                SoundByte.DUMBSHITPLEB,
                SoundByte.ELEGIGGLE,
                SoundByte.EXORT,
                SoundByte.FEELSBADMAN,
                SoundByte.FEELSGOODMAN,
                SoundByte.FOURHEAD,
                SoundByte.GODDAMNBABOONS,
                SoundByte.GODDAMNPEPEGA,
                SoundByte.GOROSH,
                SoundByte.HOBBITS,
                SoundByte.ICARE,
                SoundByte.IMRATTING,
                SoundByte.KREYGASM,
                SoundByte.MOTHERCOMES,
                SoundByte.MOVEYOURASS,
                SoundByte.NUTS,
                SoundByte.OHBABY,
                SoundByte.OHMYGOD,
                SoundByte.OHNONONO,
                SoundByte.OMEGAEZ,
                SoundByte.PERMABAN,
                SoundByte.PLEBSAREDISGUSTING,
                SoundByte.PRAISE,
                SoundByte.QUAS,
                SoundByte.RED,
                SoundByte.RONNIE,
                SoundByte.SAUSAGE,
                SoundByte.SEEYA,
                SoundByte.SIKE,
                SoundByte.SLOWDOWN,
                SoundByte.SURPRISE,
                SoundByte.SYNDLUL,
                SoundByte.UGANDA,
                SoundByte.WARRIA,
                SoundByte.WEX,
                SoundByte.WHYDIDYOUCOME,
                SoundByte.YEEHAW,
                SoundByte.YIKES,
                SoundByte.YOUKILLEDMYPEOPLE)
    }
}