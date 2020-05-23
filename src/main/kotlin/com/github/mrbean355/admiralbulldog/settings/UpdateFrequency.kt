package com.github.mrbean355.admiralbulldog.settings

import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.settings.UpdateFrequency.*
import javafx.util.StringConverter
import java.util.concurrent.TimeUnit.DAYS

enum class UpdateFrequency {
    ALWAYS,
    DAILY,
    WEEKLY,
    MONTHLY,
    NEVER
}

/**
 * @return `true` if the time since [lastCheck] is >= the duration represented by `this` [UpdateFrequency].
 */
fun UpdateFrequency.lessThanTimeSince(lastCheck: Long): Boolean {
    val timeSinceLastCheck = System.currentTimeMillis() - lastCheck
    val checkAfterDuration = when (this) {
        ALWAYS -> Long.MIN_VALUE
        DAILY -> DAYS.toMillis(1)
        WEEKLY -> DAYS.toMillis(7)
        MONTHLY -> DAYS.toMillis(30)
        NEVER -> Long.MAX_VALUE
    }
    return timeSinceLastCheck >= checkAfterDuration
}

class UpdateFrequencyStringConverter : StringConverter<UpdateFrequency>() {

    override fun toString(obj: UpdateFrequency?): String? {
        obj ?: return null
        return when (obj) {
            ALWAYS -> getString("frequency_always")
            DAILY -> getString("frequency_daily")
            WEEKLY -> getString("frequency_weekly")
            MONTHLY -> getString("frequency_monthly")
            NEVER -> getString("frequency_never")
        }
    }

    override fun fromString(str: String?): UpdateFrequency? {
        return values().firstOrNull { toString(it) == str }
    }
}
