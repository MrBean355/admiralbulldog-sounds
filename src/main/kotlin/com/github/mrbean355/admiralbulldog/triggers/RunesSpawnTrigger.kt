package com.github.mrbean355.admiralbulldog.triggers

import com.github.mrbean355.admiralbulldog.game.GameState
import com.github.mrbean355.dota2.map.MatchState
import kotlin.math.ceil

abstract class RunesSpawnTrigger(
    frequencyMinutes: Int,
    spawnsAtStart: Boolean,
    private val provideWarningPeriod: () -> Int,
) : SoundTrigger {

    private val frequencySeconds = frequencyMinutes * 60
    private val firstSpawnTime = if (spawnsAtStart) 0 else frequencySeconds
    private var nextSpawnTime = UNINITIALISED

    override fun shouldPlay(previous: GameState, current: GameState): Boolean {
        val gameState = current.map.matchState
        if (gameState != MatchState.PreGame && gameState != MatchState.GameInProgress) {
            // Don't play during the picking phase.
            return false
        }
        val currentTime = current.map.clockTime
        if (nextSpawnTime == UNINITIALISED) {
            nextSpawnTime = findNextSpawnTime(currentTime)
        }

        val warningPeriod = provideWarningPeriod()
        val nextPlayTime = nextSpawnTime - warningPeriod

        if (currentTime >= nextPlayTime) {
            val diff = currentTime - nextPlayTime
            nextSpawnTime += frequencySeconds
            if (diff <= warningPeriod) {
                return true
            }
        }
        return false
    }

    private fun findNextSpawnTime(clockTime: Int): Int {
        val iteration = ceil(clockTime / frequencySeconds.toFloat()).toInt()
        val nextPlayTime = iteration * frequencySeconds
        return nextPlayTime.coerceAtLeast(firstSpawnTime)
    }
}