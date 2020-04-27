package com.github.mrbean355.admiralbulldog.ui2.triggers

import com.github.mrbean355.admiralbulldog.game.GameState
import com.github.mrbean355.admiralbulldog.ui.getString

object OnRespawn : SoundTrigger {
    override val name = getString("trigger_name_respawn")
    override val description = getString("trigger_desc_respawn")

    override fun didHappen(previous: GameState, current: GameState): Boolean {
        return previous.hero!!.respawn_seconds > 0 && current.hero!!.respawn_seconds == 0
    }
}
