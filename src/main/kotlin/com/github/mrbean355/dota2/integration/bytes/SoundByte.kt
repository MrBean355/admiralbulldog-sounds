package com.github.mrbean355.dota2.integration.bytes

import com.github.mrbean355.dota2.integration.GameState
import com.github.mrbean355.dota2.integration.assets.SoundFile

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
    /** Pick a random sound byte to play. */
    val choices: Collection<SoundFile>

    /** Examine the states and decide if a should should be played. */
    fun shouldPlay(previous: GameState, current: GameState): Boolean
}

interface RandomSoundByte : SoundByte {
    /** Chance to play a sound byte, from 0 to 1. */
    val chance: Float
}