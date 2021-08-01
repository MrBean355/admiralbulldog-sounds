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
import com.github.mrbean355.admiralbulldog.stringResource
import kotlin.random.Random

/**
 * Triggers every few minutes of game time.
 */
object Periodically : SoundTrigger {
    private var nextPlayClockTime = NO_VALUE

    override val name get() = stringResource("trigger_name_periodically")

    override fun shouldPlay(previous: GameState, current: GameState): Boolean {
        if (nextPlayClockTime == NO_VALUE) {
            nextPlayClockTime = current.map!!.clock_time + randomiseDelay()
        } else if (current.map!!.clock_time >= nextPlayClockTime) {
            nextPlayClockTime += randomiseDelay()
            return true
        }
        return false
    }

    override fun reset() {
        nextPlayClockTime = NO_VALUE
    }

    private fun randomiseDelay(): Int {
        // Cast to double so we can randomise a non-whole amount of minutes.
        val minQuietTime = 10.0 // TODO: ConfigPersistence.getMinPeriod().toDouble()
        val maxQuietTime = 20.0 // TODO: ConfigPersistence.getMaxPeriod().toDouble()
        return if (minQuietTime != maxQuietTime) {
            Random.nextDouble(minQuietTime, maxQuietTime)
        } else {
            minQuietTime
        }.toInt() * 60
    }
}