package com.github.mrbean355.admiralbulldog.ui2.triggers

import com.github.mrbean355.admiralbulldog.game.GameState
import com.github.mrbean355.admiralbulldog.ui.getString

object OnKill : SoundTrigger {
    override val name = getString("trigger_name_kill")
    override val description = getString("trigger_desc_kill")

    override fun didHappen(previous: GameState, current: GameState): Boolean {
        return current.player!!.kills > previous.player!!.kills
    }
}
