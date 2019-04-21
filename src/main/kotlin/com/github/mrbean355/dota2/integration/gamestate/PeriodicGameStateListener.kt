package com.github.mrbean355.dota2.integration.gamestate

import com.github.mrbean355.dota2.integration.GameState
import com.github.mrbean355.dota2.integration.SoundEffect
import com.github.mrbean355.dota2.integration.gamestate.util.UNINITIALISED
import com.github.mrbean355.dota2.integration.gamestate.util.random

/** Random sound effects as time goes on. */
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
                SoundEffect.ADMIRALC,
                SoundEffect.AHAHA4HEAD,
                SoundEffect.ALLIANCE,
                SoundEffect.BADGRILL,
                SoundEffect.BRUH,
                SoundEffect.CATERPILLAR,
                SoundEffect.CHEFS4,
                SoundEffect.CLICKFORFREEPOINTS,
                SoundEffect.COLLATERALDAMAGE,
                SoundEffect.DAMNSON,
                SoundEffect.DENMARK,
                SoundEffect.DUMBSHITPLEB,
                SoundEffect.ELEGIGGLE,
                SoundEffect.EXORT,
                SoundEffect.FEELSBADMAN,
                SoundEffect.FEELSGOODMAN,
                SoundEffect.FOURHEAD,
                SoundEffect.GODDAMNBABOONS,
                SoundEffect.GODDAMNPEPEGA,
                SoundEffect.GOROSH,
                SoundEffect.HOBBITS,
                SoundEffect.ICARE,
                SoundEffect.IMRATTING,
                SoundEffect.KREYGASM,
                SoundEffect.MOTHERCOMES,
                SoundEffect.MOVEYOURASS,
                SoundEffect.NUTS,
                SoundEffect.OHBABY,
                SoundEffect.OHMYGOD,
                SoundEffect.OHNONONO,
                SoundEffect.OMEGAEZ,
                SoundEffect.PERMABAN,
                SoundEffect.PLEBSAREDISGUSTING,
                SoundEffect.PRAISE,
                SoundEffect.QUAS,
                SoundEffect.RED,
                SoundEffect.RONNIE,
                SoundEffect.SAUSAGE,
                SoundEffect.SEEYA,
                SoundEffect.SIKE,
                SoundEffect.SLOWDOWN,
                SoundEffect.SURPRISE,
                SoundEffect.SYNDLUL,
                SoundEffect.UGANDA,
                SoundEffect.WARRIA,
                SoundEffect.WEX,
                SoundEffect.WHYDIDYOUCOME,
                SoundEffect.YEEHAW,
                SoundEffect.YIKES,
                SoundEffect.YOUKILLEDMYPEOPLE)
    }
}