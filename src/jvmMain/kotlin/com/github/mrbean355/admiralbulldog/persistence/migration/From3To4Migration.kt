package com.github.mrbean355.admiralbulldog.persistence.migration

import com.google.gson.JsonArray
import com.google.gson.JsonObject

class From3To4Migration : Migration(from = 3, to = 4) {

    override fun migrate(config: JsonObject) {
        // New property: wisdom rune timer.
        config.getAsJsonObject("special")["wisdomRuneTimer"] = 45

        // New triggers: OnWisdomRunesSpawn, OnPause, RoshanTimer.
        config.getAsJsonObject("sounds").apply {
            add("OnWisdomRunesSpawn", triggerConfig())
            add("OnPause", triggerConfig())
            add("RoshanTimer", triggerConfig())
        }
    }

    private fun triggerConfig() = JsonObject().apply {
        addProperty("enabled", false)
        addProperty("chance", 100)
        addProperty("minRate", 100)
        addProperty("maxRate", 100)
        addProperty("playThroughDiscord", false)
        add("sounds", JsonArray())
    }
}