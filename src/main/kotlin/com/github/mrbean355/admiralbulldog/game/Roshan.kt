/*
 * Copyright 2023 Michael Johnston
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

package com.github.mrbean355.admiralbulldog.game

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object Roshan {
    const val AEGIS_DURATION: Int = 5 * 60
    const val MIN_RESPAWN_TIME: Int = 8 * 60
    const val MAX_RESPAWN_TIME: Int = 11 * 60

    const val AEGIS_DURATION_TURBO: Int = 4 * 60
    const val MIN_RESPAWN_TIME_TURBO: Int = MIN_RESPAWN_TIME / 2
    const val MAX_RESPAWN_TIME_TURBO: Int = MAX_RESPAWN_TIME / 2

    private val _deathTime: MutableStateFlow<Int?> = MutableStateFlow(null)

    val deathTime: StateFlow<Int?> = _deathTime.asStateFlow()

    fun setDeathTime(deathTime: Int) {
        _deathTime.value = deathTime
    }

    fun reset() {
        _deathTime.value = null
    }
}