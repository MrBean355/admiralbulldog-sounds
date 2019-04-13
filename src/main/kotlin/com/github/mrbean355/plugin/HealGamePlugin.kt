package com.github.mrbean355.plugin

import com.github.mrbean355.GameState
import com.github.mrbean355.SoundEffect
import com.github.mrbean355.plugin.util.oneInThreeChance

/** Sound effect on heal (sometimes). */
class HealGamePlugin : GamePlugin {
    private var coolDownUntil = -1L

    override fun onGameStateUpdated(previousState: GameState, newState: GameState) {
        val currentTime = System.currentTimeMillis()
        if (currentTime < coolDownUntil) {
            return
        }
        if (previousState.hero!!.health > 0F &&
                newState.hero!!.max_health == previousState.hero.max_health &&
                newState.hero.health - previousState.hero.health >= 200 &&
                newState.hero.health_percent - previousState.hero.health_percent > 5 &&
                oneInThreeChance()) {

            coolDownUntil = currentTime + COOL_DOWN_PERIOD
            SoundEffect.EEL.play()
        }
    }

    private companion object {
        private const val COOL_DOWN_PERIOD = 60 * 1000
    }
}