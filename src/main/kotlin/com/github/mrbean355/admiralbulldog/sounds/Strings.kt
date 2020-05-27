package com.github.mrbean355.admiralbulldog.sounds

import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.triggers.OnBountyRunesSpawn
import com.github.mrbean355.admiralbulldog.triggers.OnDeath
import com.github.mrbean355.admiralbulldog.triggers.OnDefeat
import com.github.mrbean355.admiralbulldog.triggers.OnHeal
import com.github.mrbean355.admiralbulldog.triggers.OnKill
import com.github.mrbean355.admiralbulldog.triggers.OnMatchStart
import com.github.mrbean355.admiralbulldog.triggers.OnMidasReady
import com.github.mrbean355.admiralbulldog.triggers.OnRespawn
import com.github.mrbean355.admiralbulldog.triggers.OnSmoked
import com.github.mrbean355.admiralbulldog.triggers.OnVictory
import com.github.mrbean355.admiralbulldog.triggers.Periodically
import com.github.mrbean355.admiralbulldog.triggers.SoundTriggerType

val SoundTriggerType.friendlyName: String
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

val SoundTriggerType.description: String
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

val SoundTriggerType.tableHeader: String
    get() = when (this) {
        OnBountyRunesSpawn::class -> getString("trigger_header_bounty_runes")
        OnDeath::class -> getString("trigger_header_death")
        OnDefeat::class -> getString("trigger_header_defeat")
        OnHeal::class -> getString("trigger_header_heal")
        OnKill::class -> getString("trigger_header_kill")
        OnMatchStart::class -> getString("trigger_header_match_start")
        OnMidasReady::class -> getString("trigger_header_midas_ready")
        OnRespawn::class -> getString("trigger_header_respawn")
        OnSmoked::class -> getString("trigger_header_smoked")
        OnVictory::class -> getString("trigger_header_victory")
        Periodically::class -> getString("trigger_header_periodically")
        else -> throw IllegalArgumentException("Unexpected type: $this")
    }