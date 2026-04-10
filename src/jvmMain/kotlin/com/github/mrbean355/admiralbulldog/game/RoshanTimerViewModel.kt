package com.github.mrbean355.admiralbulldog.game

import com.github.mrbean355.admiralbulldog.arch.ComposeViewModel
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.common.showInformation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

class RoshanTimerViewModel : ComposeViewModel() {

    private val _deathTime = MutableStateFlow("")
    val deathTime = _deathTime.asStateFlow()

    private val _aegisExpiryTime = MutableStateFlow("")
    val aegisExpiryTime = _aegisExpiryTime.asStateFlow()

    private val _respawnTime = MutableStateFlow("")
    val respawnTime = _respawnTime.asStateFlow()

    private val _aegisExpiryTimeTurbo = MutableStateFlow("")
    val aegisExpiryTimeTurbo = _aegisExpiryTimeTurbo.asStateFlow()

    private val _respawnTimeTurbo = MutableStateFlow("")
    val respawnTimeTurbo = _respawnTimeTurbo.asStateFlow()

    init {
        viewModelScope.launch {
            Roshan.deathTime.collectLatest {
                _deathTime.value = getString("label_roshan_timer_death", it.formatTime(offset = 0))
                _aegisExpiryTime.value = getString("label_roshan_timer_aegis", it.formatTime(offset = Roshan.AEGIS_DURATION))
                _respawnTime.value = getString("label_roshan_timer_respawn", it.formatTime(offset = Roshan.MIN_RESPAWN_TIME), it.formatTime(offset = Roshan.MAX_RESPAWN_TIME))
                _aegisExpiryTimeTurbo.value = getString("label_roshan_timer_aegis", it.formatTime(offset = Roshan.AEGIS_DURATION_TURBO))
                _respawnTimeTurbo.value = getString("label_roshan_timer_respawn", it.formatTime(offset = Roshan.MIN_RESPAWN_TIME_TURBO), it.formatTime(offset = Roshan.MAX_RESPAWN_TIME_TURBO))
            }
        }
    }

    fun onHelpClicked() {
        showInformation(getString("header_roshan_timer_help"), getString("content_roshan_timer_help"))
    }

    private fun Int?.formatTime(offset: Int): String {
        this ?: return "?"

        val time = plus(offset)
        val minutes = time.absoluteValue / 60
        val seconds = time.absoluteValue - minutes * 60

        return buildString {
            if (time < 0) {
                append('-')
            }
            append(minutes)
                .append(':')
                .append(seconds.toString().padStart(2, '0'))
        }
    }
}