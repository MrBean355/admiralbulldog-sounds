package com.github.mrbean355.admiralbulldog.ui2.triggers

import com.github.mrbean355.admiralbulldog.game.GameState
import com.github.mrbean355.admiralbulldog.ui.getString

object OnSmoked : SoundTrigger {
    override val name = getString("trigger_name_smoked")
    override val description = getString("trigger_desc_smoked")

    override fun didHappen(previous: GameState, current: GameState): Boolean {
        return !previous.hero!!.smoked && current.hero!!.smoked
    }
}
