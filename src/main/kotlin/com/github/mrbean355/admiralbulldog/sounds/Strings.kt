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

package com.github.mrbean355.admiralbulldog.sounds

import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.triggers.OnBountyRunesSpawn
import com.github.mrbean355.admiralbulldog.triggers.OnDeath
import com.github.mrbean355.admiralbulldog.triggers.OnDefeat
import com.github.mrbean355.admiralbulldog.triggers.OnHeal
import com.github.mrbean355.admiralbulldog.triggers.OnKill
import com.github.mrbean355.admiralbulldog.triggers.OnMatchStart
import com.github.mrbean355.admiralbulldog.triggers.OnMidasReady
import com.github.mrbean355.admiralbulldog.triggers.OnPause
import com.github.mrbean355.admiralbulldog.triggers.OnRespawn
import com.github.mrbean355.admiralbulldog.triggers.OnSmoked
import com.github.mrbean355.admiralbulldog.triggers.OnVictory
import com.github.mrbean355.admiralbulldog.triggers.OnWisdomRunesSpawn
import com.github.mrbean355.admiralbulldog.triggers.Periodically
import com.github.mrbean355.admiralbulldog.triggers.RoshanTimer
import com.github.mrbean355.admiralbulldog.triggers.SoundTriggerType

val SoundTriggerType.friendlyName: String
    get() = when (this) {
        OnBountyRunesSpawn::class -> getString("trigger_name_bounty_runes")
        OnWisdomRunesSpawn::class -> getString("trigger_name_wisdom_runes")
        OnDeath::class -> getString("trigger_name_death")
        OnDefeat::class -> getString("trigger_name_defeat")
        OnHeal::class -> getString("trigger_name_heal")
        OnKill::class -> getString("trigger_name_kill")
        OnMatchStart::class -> getString("trigger_name_match_start")
        OnMidasReady::class -> getString("trigger_name_midas_ready")
        OnPause::class -> getString("trigger_name_paused")
        OnRespawn::class -> getString("trigger_name_respawn")
        OnSmoked::class -> getString("trigger_name_smoked")
        OnVictory::class -> getString("trigger_name_victory")
        Periodically::class -> getString("trigger_name_periodically")
        RoshanTimer::class -> getString("trigger_name_roshan")
        else -> throw IllegalArgumentException("Unexpected type: $this")
    }

val SoundTriggerType.description: String
    get() = when (this) {
        OnBountyRunesSpawn::class -> getString("trigger_desc_bounty_runes")
        OnWisdomRunesSpawn::class -> getString("trigger_desc_wisdom_runes")
        OnDeath::class -> getString("trigger_desc_death")
        OnDefeat::class -> getString("trigger_desc_defeat")
        OnHeal::class -> getString("trigger_desc_heal")
        OnKill::class -> getString("trigger_desc_kill")
        OnMatchStart::class -> getString("trigger_desc_match_start")
        OnMidasReady::class -> getString("trigger_desc_midas_ready")
        OnPause::class -> getString("trigger_desc_paused")
        OnRespawn::class -> getString("trigger_desc_respawn")
        OnSmoked::class -> getString("trigger_desc_smoked")
        OnVictory::class -> getString("trigger_desc_victory")
        Periodically::class -> getString("trigger_desc_periodically")
        RoshanTimer::class -> getString("trigger_desc_roshan")
        else -> throw IllegalArgumentException("Unexpected type: $this")
    }

val SoundTriggerType.tableHeader: String
    get() = when (this) {
        OnBountyRunesSpawn::class -> getString("trigger_header_bounty_runes")
        OnWisdomRunesSpawn::class -> getString("trigger_header_wisdom_runes")
        OnDeath::class -> getString("trigger_header_death")
        OnDefeat::class -> getString("trigger_header_defeat")
        OnHeal::class -> getString("trigger_header_heal")
        OnKill::class -> getString("trigger_header_kill")
        OnMatchStart::class -> getString("trigger_header_match_start")
        OnMidasReady::class -> getString("trigger_header_midas_ready")
        OnPause::class -> getString("trigger_header_paused")
        OnRespawn::class -> getString("trigger_header_respawn")
        OnSmoked::class -> getString("trigger_header_smoked")
        OnVictory::class -> getString("trigger_header_victory")
        Periodically::class -> getString("trigger_header_periodically")
        RoshanTimer::class -> getString("trigger_header_roshan")
        else -> throw IllegalArgumentException("Unexpected type: $this")
    }