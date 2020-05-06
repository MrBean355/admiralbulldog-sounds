package com.github.mrbean355.admiralbulldog.ui2.update

import com.github.mrbean355.admiralbulldog.ui.getString
import javafx.util.StringConverter

enum class UpdateFrequency {
    ALWAYS,
    DAILY,
    WEEKLY,
    MONTHLY,
    NEVER
}

val UpdateFrequency.friendlyName: String
    get() = getString(when (this) {
        UpdateFrequency.ALWAYS -> "update_frequency_always"
        UpdateFrequency.DAILY -> "update_frequency_daily"
        UpdateFrequency.WEEKLY -> "update_frequency_weekly"
        UpdateFrequency.MONTHLY -> "update_frequency_monthly"
        UpdateFrequency.NEVER -> "update_frequency_never"
    })

class UpdateFrequencyStringConverter : StringConverter<UpdateFrequency>() {

    override fun toString(obj: UpdateFrequency?): String? {
        return obj?.friendlyName
    }

    override fun fromString(string: String?): UpdateFrequency? {
        return UpdateFrequency.values().find { it.friendlyName == string }
    }
}
