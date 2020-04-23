package com.github.mrbean355.admiralbulldog.ui2.events

import com.github.mrbean355.admiralbulldog.game.GameState
import com.github.mrbean355.admiralbulldog.ui.getString

val ALL_EVENTS = setOf(
        BountyRunes,
        Heal,
        MidasReady,
        Smoked,
        Kill,
        Death,
        Respawn,
        MatchStart,
        Victory,
        Defeat,
        Periodically
)

interface SoundEvent {
    val name: String
    val description: String

    /** @return `true` if the event happened, `false` otherwise. */
    fun didHappen(previous: GameState, current: GameState) = false

    /** Reset any state that's being kept. */
    fun reset() {}
}

object BountyRunes : SoundEvent {
    override val name = getString("event_name_bounty_runes")
    override val description = getString("event_desc_bounty_runes")
}

object Death : SoundEvent {
    override val name = getString("event_name_death")
    override val description = getString("event_desc_death")
}

object Defeat : SoundEvent {
    override val name = getString("event_name_defeat")
    override val description = getString("event_desc_defeat")
}

object Heal : SoundEvent {
    override val name = getString("event_name_heal")
    override val description = getString("event_desc_heal")
}

object Kill : SoundEvent {
    override val name = getString("event_name_kill")
    override val description = getString("event_desc_kill")
}

object MatchStart : SoundEvent {
    override val name = getString("event_name_match_start")
    override val description = getString("event_desc_match_start")
}

object MidasReady : SoundEvent {
    override val name = getString("event_name_midas_ready")
    override val description = getString("event_desc_midas_ready")
}

object Respawn : SoundEvent {
    override val name = getString("event_name_respawn")
    override val description = getString("event_desc_respawn")
}

object Smoked : SoundEvent {
    override val name = getString("event_name_smoked")
    override val description = getString("event_desc_smoked")
}

object Victory : SoundEvent {
    override val name = getString("event_name_victory")
    override val description = getString("event_desc_victory")
}

object Periodically : SoundEvent {
    override val name = getString("event_name_periodically")
    override val description = getString("event_desc_periodically")
}
