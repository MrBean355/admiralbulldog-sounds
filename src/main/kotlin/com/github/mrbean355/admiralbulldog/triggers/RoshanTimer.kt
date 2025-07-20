package com.github.mrbean355.admiralbulldog.triggers

import com.github.mrbean355.admiralbulldog.game.GameState
import com.github.mrbean355.admiralbulldog.game.Roshan

class RoshanTimer : SoundTrigger {
    private var minRespawnTime = Int.MAX_VALUE

    override fun shouldPlay(previous: GameState, current: GameState): Boolean {
        val event = current.events.find { it.eventType == "roshan_killed" }

        if (event != null) {
            val eventClockTime = event.gameTime - (current.map.gameTime - current.map.clockTime)
            val lastDeathTime = Roshan.deathTime.value ?: Int.MIN_VALUE

            if (eventClockTime > lastDeathTime) {
                Roshan.setDeathTime(eventClockTime)
                minRespawnTime = eventClockTime + Roshan.MIN_RESPAWN_TIME
            }
        }

        if (current.map.clockTime >= minRespawnTime) {
            minRespawnTime = Int.MAX_VALUE
            return true
        }

        return false
    }
}