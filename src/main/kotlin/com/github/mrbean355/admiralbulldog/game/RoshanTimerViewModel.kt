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

import com.github.mrbean355.admiralbulldog.arch.AppViewModel
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.common.showInformation
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import tornadofx.stringProperty
import kotlin.math.absoluteValue

class RoshanTimerViewModel : AppViewModel() {

    val deathTime = stringProperty()
    val aegisExpiryTime = stringProperty()
    val respawnTime = stringProperty()
    val aegisExpiryTimeTurbo = stringProperty()
    val respawnTimeTurbo = stringProperty()

    override fun onReady() {
        viewModelScope.launch {
            Roshan.deathTime.collectLatest {
                deathTime.value = getString("label_roshan_timer_death", it.formatTime(offset = 0))
                aegisExpiryTime.value = getString("label_roshan_timer_aegis", it.formatTime(offset = Roshan.AEGIS_DURATION))
                respawnTime.value = getString("label_roshan_timer_respawn", it.formatTime(offset = Roshan.MIN_RESPAWN_TIME), it.formatTime(offset = Roshan.MAX_RESPAWN_TIME))
                aegisExpiryTimeTurbo.value = getString("label_roshan_timer_aegis", it.formatTime(offset = Roshan.AEGIS_DURATION_TURBO))
                respawnTimeTurbo.value = getString("label_roshan_timer_respawn", it.formatTime(offset = Roshan.MIN_RESPAWN_TIME_TURBO), it.formatTime(offset = Roshan.MAX_RESPAWN_TIME_TURBO))
            }
        }
    }

    fun onHelpClicked() {
        showInformation(getString("header_roshan_timer_help"), getString("content_roshan_timer_help"))
    }

    private fun Int?.formatTime(offset: Int): String {
        this ?: return "?"

        val time = plus(offset)
        val minutes = time.absoluteValue / 60
        val seconds = time.absoluteValue - minutes * 60

        return buildString {
            if (time < 0) {
                append('-')
            }
            append(minutes)
                .append(':')
                .append(seconds.toString().padStart(2, '0'))
        }
    }
}