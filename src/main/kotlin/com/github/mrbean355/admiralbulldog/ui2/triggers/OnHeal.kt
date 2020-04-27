package com.github.mrbean355.admiralbulldog.ui2.triggers

import com.github.mrbean355.admiralbulldog.events.random
import com.github.mrbean355.admiralbulldog.game.GameState
import com.github.mrbean355.admiralbulldog.ui.getString

/** Must have healed at least this much percentage. */
private const val MIN_HP_PERCENTAGE = 5

/** Health required for max chance to play the sound. */
private const val MAX_HEAL = 500

object OnHeal : SoundTrigger {
    override val name = getString("trigger_name_heal")
    override val description = getString("trigger_desc_heal")

    override fun didHappen(previous: GameState, current: GameState): Boolean {
        if (previous.hero!!.health <= 0F) {
            // We get healed on respawn; ignore.
            return false
        }
        if (current.hero!!.health_percent - previous.hero.health_percent < MIN_HP_PERCENTAGE) {
            // Small heal; ignore.
            return false
        }
        return random.nextFloat() <= (current.hero.health - previous.hero.health) / MAX_HEAL
    }
}
