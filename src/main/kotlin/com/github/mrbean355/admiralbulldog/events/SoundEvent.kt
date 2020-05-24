package com.github.mrbean355.admiralbulldog.events

import com.github.mrbean355.admiralbulldog.game.GameState

val SOUND_EVENT_TYPES = setOf(
        OnBountyRunesSpawn::class,
        OnDeath::class,
        OnDefeat::class,
        OnHeal::class,
        OnKill::class,
        OnMatchStart::class,
        OnMidasReady::class,
        OnRespawn::class,
        OnSmoked::class,
        OnVictory::class,
        Periodically::class)

interface SoundEvent {
    /** Examine the states and decide if a should should be played. */
    fun shouldPlay(previous: GameState, current: GameState): Boolean
}
