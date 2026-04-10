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
        return name == "item_hand_of_midas" && cooldown == 0
    }

    private fun Item.isMidasOnCooldown(): Boolean {
        val cd = cooldown
        return name == "item_hand_of_midas" && cd != null && cd > 0
    }
}