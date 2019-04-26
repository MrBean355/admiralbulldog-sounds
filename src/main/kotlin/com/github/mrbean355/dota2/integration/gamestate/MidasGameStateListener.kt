package com.github.mrbean355.dota2.integration.gamestate

import com.github.mrbean355.dota2.integration.GameState
import com.github.mrbean355.dota2.integration.Item
import com.github.mrbean355.dota2.integration.assets.SoundByte

/** Sound byte when Hand of Midas is off cooldown. */
class MidasGameStateListener : GameStateListener {
    private var played = false

    override fun onGameStateUpdated(previousState: GameState, newState: GameState) {
        checkInventorySlot(newState.items!!.slot0)
        checkInventorySlot(newState.items.slot1)
        checkInventorySlot(newState.items.slot2)
        checkInventorySlot(newState.items.slot3)
        checkInventorySlot(newState.items.slot4)
        checkInventorySlot(newState.items.slot5)
    }

    private fun checkInventorySlot(item: Item?) {
        if (item != null && item.name == "item_hand_of_midas") {
            if (item.cooldown == 0F) {
                if (!played) {
                    played = true
                    SoundByte.USE_MIDAS.play()
                }
            } else {
                played = false
            }
        }
    }
}