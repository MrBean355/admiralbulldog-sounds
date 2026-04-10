package com.github.mrbean355.admiralbulldog.persistence.migration

import com.google.gson.JsonObject
import java.util.concurrent.TimeUnit

class From2To3Migration : Migration(from = 2, to = 3) {

    override fun migrate(config: JsonObject) {
        val feedbackCompleted = config["feedbackCompleted"]?.asJsonPrimitive?.asLong ?: 0
        config.remove("feedbackCompleted")
        config["nextFeedback"] = feedbackCompleted + TimeUnit.DAYS.toMillis(90)
    }
}