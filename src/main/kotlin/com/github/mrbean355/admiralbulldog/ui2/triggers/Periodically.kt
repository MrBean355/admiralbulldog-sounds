package com.github.mrbean355.admiralbulldog.ui2.triggers

import com.github.mrbean355.admiralbulldog.events.UNINITIALISED
import com.github.mrbean355.admiralbulldog.events.random
import com.github.mrbean355.admiralbulldog.game.GameState
import com.github.mrbean355.admiralbulldog.ui.getString

private const val MIN_QUIET_TIME_SECS = 300 // 5 minutes
private const val MAX_QUIET_TIME_SECS = 900 // 15 minutes

object Periodically : SoundTrigger {
    override val name = getString("trigger_name_periodically")
    override val description = getString("trigger_desc_periodically")
    private var nextPlayClockTime = UNINITIALISED

    override fun didHappen(previous: GameState, current: GameState): Boolean {
        if (nextPlayClockTime == UNINITIALISED) {
            nextPlayClockTime = current.map!!.clock_time + random.nextInt(MIN_QUIET_TIME_SECS, MAX_QUIET_TIME_SECS)
        } else if (current.map!!.clock_time >= nextPlayClockTime) {
            nextPlayClockTime += random.nextInt(MIN_QUIET_TIME_SECS, MAX_QUIET_TIME_SECS)
            return true
        }
        return false
    }

    override fun reset() {
        nextPlayClockTime = UNINITIALISED
    }
}
