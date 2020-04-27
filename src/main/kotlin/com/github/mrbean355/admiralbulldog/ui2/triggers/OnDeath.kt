package com.github.mrbean355.admiralbulldog.ui2.triggers

import com.github.mrbean355.admiralbulldog.game.GameState
import com.github.mrbean355.admiralbulldog.ui.getString

object OnDeath : SoundTrigger {
    override val name = getString("trigger_name_death")
    override val description = getString("trigger_desc_death")

    override fun didHappen(previous: GameState, current: GameState): Boolean {
        return current.player!!.deaths > previous.player!!.deaths
    }
}
