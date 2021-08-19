/*
 * Copyright 2021 Michael Johnston
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.mrbean355.admiralbulldog.sounds.triggers

import com.github.mrbean355.admiralbulldog.gsi.GameState
import com.github.mrbean355.admiralbulldog.gsi.MatchState
import com.github.mrbean355.admiralbulldog.stringResource
import kotlin.math.ceil

/** How often (in seconds) the runes spawn. */
private const val HOW_OFTEN = 3 * 60L

/**
 * Triggers shortly before the bounty runes spawn.
 */
object OnBountyRunesSpawn : SoundTrigger {
    /** Play a sound this many seconds before the bounty runes spawn. */
    private val warningPeriod = 15L // TODO: ConfigPersistence.getBountyRuneTimer().toLong()
    private var nextPlayTime = NO_VALUE

    override val name get() = stringResource("trigger_name_bounty_runes")
    override val description get() = stringResource("trigger_desc_bounty_runes")

    override fun shouldPlay(previous: GameState, current: GameState): Boolean {
        val gameState = current.map!!.game_state
        if (gameState != MatchState.DOTA_GAMERULES_STATE_PRE_GAME && gameState != MatchState.DOTA_GAMERULES_STATE_GAME_IN_PROGRESS) {
            // Don't play during the picking phase.
            return false
        }
        val currentTime = current.map.clock_time
        if (nextPlayTime == NO_VALUE) {
            nextPlayTime = findNextPlayTime(currentTime)
        }
        if (currentTime >= nextPlayTime) {
            val diff = currentTime - nextPlayTime
            nextPlayTime = findNextPlayTime(currentTime + 10)
            if (diff <= warningPeriod) {
                return true
            }
        }
        return false
    }

    override fun reset() {
        nextPlayTime = NO_VALUE
    }

    private fun findNextPlayTime(clockTime: Long): Long {
        val iteration = ceil((clockTime + warningPeriod) / HOW_OFTEN.toFloat()).toInt()
        val nextPlayTime = iteration * HOW_OFTEN - warningPeriod
        return nextPlayTime.coerceAtLeast(-warningPeriod)
    }
}