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

/**
 * Triggers just after the clock hits 0.
 */
object OnMatchStart : SoundTrigger {
    private var played = false

    override val name get() = stringResource("trigger_name_match_start")

    override fun shouldPlay(previous: GameState, current: GameState): Boolean {
        if (!played && current.map!!.game_state == MatchState.DOTA_GAMERULES_STATE_GAME_IN_PROGRESS && current.map.clock_time < 5) {
            played = true
            return true
        }
        return false
    }
}