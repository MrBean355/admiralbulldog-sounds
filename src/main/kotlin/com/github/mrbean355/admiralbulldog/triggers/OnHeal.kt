/*
 * Copyright 2024 Michael Johnston
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.mrbean355.admiralbulldog.triggers

import com.github.mrbean355.admiralbulldog.game.GameState
import kotlin.random.Random

/** Must have healed at least this much percentage. */
private const val MIN_HP_PERCENTAGE = 4

/** Health required for max chance to play the sound. */
private const val MAX_HEAL = 400

/**
 * Play a sound when the hero is healed.
 *
 * The chance increases as the heal amount increases.
 * A heal amount of [MAX_HEAL] or more gives a 100% chance to play the sound.
 * The heal amount must be at least [MIN_HP_PERCENTAGE] percent of the hero's max HP.
 */
class OnHeal : SoundTrigger {

    override fun shouldPlay(previous: GameState, current: GameState): Boolean {
        previous.hero ?: return false
        current.hero ?: return false

        if (!previous.hero.isAlive && current.hero.isAlive) {
            // We get healed on respawn; ignore.
            return false
        }

        if (current.hero.maxHealth != previous.hero.maxHealth) {
            // Ignore heals caused by increasing max HP.
            return false
        }

        if (current.hero.healthPercent - previous.hero.healthPercent < MIN_HP_PERCENTAGE) {
            // Small heal; ignore.
            return false
        }

        return true
    }

    fun doesSmartChanceProc(previous: GameState, current: GameState): Boolean {
        previous.hero ?: return false
        current.hero ?: return false

        return Random.nextFloat() <= (current.hero.health - previous.hero.health) / MAX_HEAL.toFloat()
    }
}