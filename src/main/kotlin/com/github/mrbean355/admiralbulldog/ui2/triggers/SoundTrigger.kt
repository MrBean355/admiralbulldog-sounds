package com.github.mrbean355.admiralbulldog.ui2.triggers

import com.github.mrbean355.admiralbulldog.game.GameState

interface SoundTrigger {
    val name: String
    val description: String

    /** @return `true` if the trigger happened, `false` otherwise. */
    fun didHappen(previous: GameState, current: GameState): Boolean

    /** Reset any state that's being kept. */
    fun reset() {
        // Nothing by default.
    }

    companion object {
        private val IMPLEMENTATIONS = setOf(
                OnBountyRunesSpawning,
                OnHeal,
                OnMidasReady,
                OnSmoked,
                OnKill,
                OnDeath,
                OnRespawn,
                OnMatchStart,
                OnVictory,
                OnDefeat,
                Periodically
        )

        fun getAll(): List<SoundTrigger> {
            return IMPLEMENTATIONS.toList()
        }

        fun onEach(block: (SoundTrigger) -> Unit) {
            IMPLEMENTATIONS.forEach(block)
        }
    }
}
