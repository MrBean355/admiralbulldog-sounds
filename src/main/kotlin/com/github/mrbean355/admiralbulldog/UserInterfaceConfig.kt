package com.github.mrbean355.admiralbulldog

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
    get() {
        return when (this) {
            OnBountyRunesSpawn::class -> "Bounty runes spawning"
            OnDeath::class -> "Got killed"
            OnDefeat::class -> "Lost the match"
            OnHeal::class -> "Got healed"
            OnKill::class -> "Killed a hero"
            OnMatchStart::class -> "Match starting"
            OnMidasReady::class -> "Midas is ready"
            OnRespawn::class -> "Respawned"
            OnSmoked::class -> "Used Smoke of Deceit"
            OnVictory::class -> "Won the match"
            Periodically::class -> "As time goes on"
            else -> simpleName ?: "Unknown"
        }
    }

val KClass<out SoundEvent>.description: String
    get() {
        return when (this) {
            OnBountyRunesSpawn::class -> "Plays a sound 15 seconds before bounty runes spawn."
            OnDeath::class -> "Plays a sound when you die (1 in 3 chance)."
            OnDefeat::class -> "Plays a sound when your ancient explodes."
            OnHeal::class -> "Plays a sound when you get healed for at least 200 HP (1 in 3 chance)."
            OnKill::class -> "Plays a sound when you kill an enemy (1 in 3 chance)."
            OnMatchStart::class -> "Plays a sound when the game starts (clock hits 0)."
            OnMidasReady::class -> "Plays a sound when your Hand of Midas comes off cooldown."
            OnRespawn::class -> "Plays a sound when you respawn."
            OnSmoked::class -> "Plays a sound when you are affected by Smoke of Deceit."
            OnVictory::class -> "Plays a sound when the enemy's ancient explodes."
            Periodically::class -> "Plays a sound every 5 - 15 minutes."
            else -> simpleName ?: "Unknown"
        }
    }