/*
 * Copyright 2021 Michael Johnston
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.mrbean355.admiralbulldog.sounds.triggers

import com.github.mrbean355.admiralbulldog.gsi.GameState
import com.github.mrbean355.admiralbulldog.gsi.Item
import com.github.mrbean355.admiralbulldog.gsi.Items
import com.github.mrbean355.admiralbulldog.stringResource

/**
 * Triggers when the current hero's Hand of Midas goes off cooldown.
 */
object OnMidasReady : SoundTrigger {

    override val name get() = stringResource("trigger_name_midas_ready")
    override val description get() = stringResource("trigger_desc_midas_ready")

    override fun shouldPlay(previous: GameState, current: GameState): Boolean {
        return previous.items.isMidasOnCooldown() && current.items.isMidasOffCooldown()
    }

    private fun Items?.isMidasOnCooldown(): Boolean {
        if (this == null) return false
        return slot0.isMidasOnCooldown() || slot1.isMidasOnCooldown() || slot2.isMidasOnCooldown() ||
                slot3.isMidasOnCooldown() || slot4.isMidasOnCooldown() || slot5.isMidasOnCooldown()
    }

    private fun Items?.isMidasOffCooldown(): Boolean {
        if (this == null) return false
        return slot0.isMidasOffCooldown() || slot1.isMidasOffCooldown() || slot2.isMidasOffCooldown() ||
                slot3.isMidasOffCooldown() || slot4.isMidasOffCooldown() || slot5.isMidasOffCooldown()
    }

    private fun Item?.isMidasOffCooldown(): Boolean {
        if (this == null || cooldown == null) {
            return false
        }
        return name == "item_hand_of_midas" && cooldown.compareTo(0f) == 0
    }

    private fun Item?.isMidasOnCooldown(): Boolean {
        if (this == null || cooldown == null) {
            return false
        }
        return name == "item_hand_of_midas" && cooldown.compareTo(0f) > 0
    }
}