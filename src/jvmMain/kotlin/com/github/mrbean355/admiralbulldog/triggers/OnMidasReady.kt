package com.github.mrbean355.admiralbulldog.triggers

import com.github.mrbean355.admiralbulldog.game.GameState
import com.github.mrbean355.dota2.item.Item
import com.github.mrbean355.dota2.item.Items

class OnMidasReady : SoundTrigger {

    override fun shouldPlay(previous: GameState, current: GameState): Boolean {
        previous.items ?: return false
        current.items ?: return false

        return previous.items.isMidasOnCooldown() && current.items.isMidasOffCooldown()
    }

    private fun Items.isMidasOnCooldown(): Boolean {
        return inventory.any { it.isMidasOnCooldown() }
    }

    private fun Items.isMidasOffCooldown(): Boolean {
        return inventory.any { it.isMidasOffCooldown() }
    }

    private fun Item.isMidasOffCooldown(): Boolean {
        val charges = charges ?: -1
        return name == "item_hand_of_midas" && charges > 0
    }

    private fun Item.isMidasOnCooldown(): Boolean {
        return name == "item_hand_of_midas" && charges == 0
    }
}