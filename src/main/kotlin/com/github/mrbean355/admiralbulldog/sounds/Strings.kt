package com.github.mrbean355.admiralbulldog.sounds

import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.events.OnBountyRunesSpawn
import com.github.mrbean355.admiralbulldog.events.OnDeath
import com.github.mrbean355.admiralbulldog.events.OnDefeat
import com.github.mrbean355.admiralbulldog.events.OnHeal
import com.github.mrbean355.admiralbulldog.events.OnKill
import com.github.mrbean355.admiralbulldog.events.OnMatchStart
import com.github.mrbean355.admiralbulldog.events.OnMidasReady
import com.github.mrbean355.admiralbulldog.events.OnRespawn
import com.github.mrbean355.admiralbulldog.events.OnSmoked
import com.github.mrbean355.admiralbulldog.events.OnVictory
import com.github.mrbean355.admiralbulldog.events.Periodically
import com.github.mrbean355.admiralbulldog.events.SoundEvent
import kotlin.reflect.KClass

val KClass<out SoundEvent>.friendlyName: String
    get() = when (this) {
        OnBountyRunesSpawn::class -> getString("trigger_name_bounty_runes")
        OnDeath::class -> getString("trigger_name_death")
        OnDefeat::class -> getString("trigger_name_defeat")
        OnHeal::class -> getString("trigger_name_heal")
        OnKill::class -> getString("trigger_name_kill")
        OnMatchStart::class -> getString("trigger_name_match_start")
        OnMidasReady::class -> getString("trigger_name_midas_ready")
        OnRespawn::class -> getString("trigger_name_respawn")
        OnSmoked::class -> getString("trigger_name_smoked")
        OnVictory::class -> getString("trigger_name_victory")
        Periodically::class -> getString("trigger_name_periodically")
        else -> throw IllegalArgumentException("Unexpected type: $this")
    }

val KClass<out SoundEvent>.description: String
    get() = when (this) {
        OnBountyRunesSpawn::class -> getString("trigger_desc_bounty_runes")
        OnDeath::class -> getString("trigger_desc_death")
        OnDefeat::class -> getString("trigger_desc_defeat")
        OnHeal::class -> getString("trigger_desc_heal")
        OnKill::class -> getString("trigger_desc_kill")
        OnMatchStart::class -> getString("trigger_desc_match_start")
        OnMidasReady::class -> getString("trigger_desc_midas_ready")
        OnRespawn::class -> getString("trigger_desc_respawn")
        OnSmoked::class -> getString("trigger_desc_smoked")
        OnVictory::class -> getString("trigger_desc_victory")
        Periodically::class -> getString("trigger_desc_periodically")
        else -> throw IllegalArgumentException("Unexpected type: $this")
    }
