package com.github.mrbean355.admiralbulldog.bytes

import com.github.mrbean355.admiralbulldog.game.GameState

val SOUND_BYTE_TYPES = setOf(
        OnBountyRunesSpawn::class,
        OnDeath::class,
        OnDefeat::class,
        OnHeal::class,
        OnKill::class,
        OnMatchStart::class,
        OnMidasReady::class,
        OnSmoked::class,
        OnVictory::class,
        Periodically::class)

interface SoundByte {
    /** Examine the states and decide if a should should be played. */
    fun shouldPlay(previous: GameState, current: GameState): Boolean
}

interface RandomSoundByte : SoundByte {
    /** Chance to play a sound byte, from 0 to 1. */
    val chance: Float
}