package com.github.mrbean355.dota2.integration.bytes

import com.github.mrbean355.dota2.integration.GameState
import com.github.mrbean355.dota2.integration.UNINITIALISED
import com.github.mrbean355.dota2.integration.assets.SoundFile
import java.util.concurrent.TimeUnit
import kotlin.math.ceil

class OnBountyRunesSpawn : SoundByte {
    override val choices = listOf(SoundFile.ROONS)
    private val howOften = TimeUnit.MINUTES.toSeconds(5)
    private val warningPeriod = TimeUnit.SECONDS.toSeconds(15)
    private var nextPlayTime = UNINITIALISED

    override fun shouldPlay(previous: GameState, current: GameState): Boolean {
        val currentTime = current.map!!.clock_time
        if (currentTime <= 0) {
            return false
        }
        if (nextPlayTime == UNINITIALISED) {
            nextPlayTime = findIteration(currentTime)
        }
        if (currentTime >= nextPlayTime) {
            val diff = currentTime - nextPlayTime
            nextPlayTime = findIteration(currentTime + 10)
            if (diff <= warningPeriod) {
                return true
            }
        }
        return false
    }

    private fun findIteration(clockTime: Long): Long {
        val iteration = ceil((clockTime + warningPeriod) / howOften.toFloat()).toInt()
        val nextPlayTime = iteration * howOften - warningPeriod
        if (nextPlayTime <= -warningPeriod) {
            return -warningPeriod
        }
        return nextPlayTime
    }
}