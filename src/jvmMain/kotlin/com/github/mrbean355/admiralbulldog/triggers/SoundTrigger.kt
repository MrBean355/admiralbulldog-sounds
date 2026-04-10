package com.github.mrbean355.admiralbulldog.triggers

import com.github.mrbean355.admiralbulldog.game.GameState
import kotlin.reflect.KClass

val SOUND_TRIGGER_TYPES: Set<SoundTriggerType> = setOf(
    OnBountyRunesSpawn::class,
    OnWisdomRunesSpawn::class,
    OnKill::class,
    OnDeath::class,
    OnRespawn::class,
    OnHeal::class,
    OnSmoked::class,
    OnMidasReady::class,
    RoshanTimer::class,
    OnPause::class,
    OnMatchStart::class,
    OnVictory::class,
    OnDefeat::class,
    Periodically::class,
)

typealias SoundTriggerType = KClass<out SoundTrigger>

interface SoundTrigger {

    /** Examine the states and decide if a sound should be played. */
    fun shouldPlay(previous: GameState, current: GameState): Boolean

}
