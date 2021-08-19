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

/** Must have healed at least this much percentage. */
private const val MIN_HP_PERCENTAGE = 4

/** Health required for max chance to play the sound. */
private const val MAX_HEAL = 400

/**
 * Triggers when the current hero is healed.
 *
 * The chance increases as the heal amount increases.
 * A heal amount of [MAX_HEAL] or more gives a 100% chance to play the sound.
 * The heal amount must be at least [MIN_HP_PERCENTAGE] percent of the hero's max HP.
 */
object OnHeal : SoundTrigger {

    override val name get() = stringResource("trigger_name_heal")
    override val description get() = stringResource("trigger_desc_heal")

    override fun shouldPlay(previous: GameState, current: GameState): Boolean {
        if (previous.hero!!.health <= 0F) {
            // We get healed on respawn; ignore.
            return false
        }
        if (current.hero!!.max_health != previous.hero.max_health) {
            // Ignore heals caused by increasing max HP.
            return false
        }
        if (current.hero.health_percent - previous.hero.health_percent < MIN_HP_PERCENTAGE) {
            // Small heal; ignore.
            return false
        }
        return true
    }

    fun doesSmartChanceProc(previous: GameState, current: GameState): Boolean {
        return Random.nextFloat() <= (current.hero!!.health - previous.hero!!.health) / MAX_HEAL
    }
}