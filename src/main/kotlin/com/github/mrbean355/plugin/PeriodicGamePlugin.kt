package com.github.mrbean355.plugin

import com.github.mrbean355.GameState
import com.github.mrbean355.SoundEffect
import kotlin.random.Random

/** Random sound effects as time goes on. */
class PeriodicGamePlugin : GamePlugin {
    private val random = Random(System.currentTimeMillis())
    private var nextPlayClockTime = UNINITIALISED

    override fun onGameStateUpdated(previousState: GameState, newState: GameState) {
        if (nextPlayClockTime == UNINITIALISED) {
            nextPlayClockTime = newState.map!!.clock_time + random.nextInt(MIN, MAX)
        } else if (newState.map!!.clock_time >= nextPlayClockTime) {
            nextPlayClockTime += random.nextInt(MIN, MAX)
            listOf(SoundEffect.SLOW_DOWN,
                    SoundEffect.ELEGIGGLE,
                    SoundEffect.MOVE_YOUR_ASS,
                    SoundEffect.PRAISE,
                    SoundEffect.SAUSAGE,
                    SoundEffect.BERRY_DISGUSTING,
                    SoundEffect.NUTS,
                    SoundEffect.PEPEGA,
                    SoundEffect.ALLIANCE,
                    SoundEffect.BABOONS,
                    SoundEffect.FEELS_BAD_MAN,
                    SoundEffect.PLEBS_ARE_DISGUSTING).random().play()
        }
    }

    private companion object {
        private const val UNINITIALISED = -1L
        private const val MIN = 300
        private const val MAX = 600
    }
}