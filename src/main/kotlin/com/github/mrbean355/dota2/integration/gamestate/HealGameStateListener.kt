package com.github.mrbean355.dota2.integration.gamestate

import com.github.mrbean355.dota2.integration.GameState
import com.github.mrbean355.dota2.integration.assets.SoundByte
import com.github.mrbean355.dota2.integration.gamestate.util.UNINITIALISED
import com.github.mrbean355.dota2.integration.gamestate.util.oneInThreeChance

/** Sound byte on heal (sometimes). */
class HealGameStateListener : GameStateListener {
    private var coolDownUntil = UNINITIALISED

    override fun onGameStateUpdated(previousState: GameState, newState: GameState) {
        val currentTime = newState.map!!.clock_time
        if (currentTime < coolDownUntil) {
            return
        }
        if (previousState.hero!!.health > 0F &&
                newState.hero!!.max_health == previousState.hero.max_health &&
                newState.hero.health - previousState.hero.health >= 200 &&
                newState.hero.health_percent - previousState.hero.health_percent > 5 &&
                oneInThreeChance()) {

            coolDownUntil = currentTime + COOL_DOWN_PERIOD
            SoundByte.EEL.play()
        }
    }

    override fun reset() {
        coolDownUntil = UNINITIALISED
    }

    private companion object {
        private const val COOL_DOWN_PERIOD = 30 // seconds
    }
}