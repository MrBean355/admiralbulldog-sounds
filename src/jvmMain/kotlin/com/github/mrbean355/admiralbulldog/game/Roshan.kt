package com.github.mrbean355.admiralbulldog.game

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object Roshan {
    const val AEGIS_DURATION: Int = 5 * 60
    const val MIN_RESPAWN_TIME: Int = 8 * 60
    const val MAX_RESPAWN_TIME: Int = 11 * 60

    const val AEGIS_DURATION_TURBO: Int = 4 * 60
    const val MIN_RESPAWN_TIME_TURBO: Int = MIN_RESPAWN_TIME / 2
    const val MAX_RESPAWN_TIME_TURBO: Int = MAX_RESPAWN_TIME / 2

    private val _deathTime: MutableStateFlow<Int?> = MutableStateFlow(null)

    val deathTime: StateFlow<Int?> = _deathTime.asStateFlow()

    fun setDeathTime(deathTime: Int) {
        _deathTime.value = deathTime
    }

    fun reset() {
        _deathTime.value = null
    }
}