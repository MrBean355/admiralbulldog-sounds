package com.github.mrbean355.admiralbulldog.persistence.migration

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser.parseString
import com.google.gson.JsonPrimitive

class From0To1Migration : Migration(from = 0, to = 1) {

    override fun migrate(config: JsonObject) {
        doLegacyMigration(config)

        // Volume changed from double to int.
        config.getAsJsonPrimitive("volume").let {
            config.add("volume", JsonPrimitive(it.asDouble.toInt()))
        }

        // New property: updates.
        config.add("updates", parseString("{ \"appUpdateCheck\": 0, \"appUpdateFrequency\": \"WEEKLY\", \"soundsUpdateFrequency\": \"DAILY\", \"modUpdateCheck\": 0, \"modUpdateFrequency\": \"ALWAYS\" }"))

        // New "sounds" object properties: chance, minRate, maxRate.
        config.getAsJsonObject("sounds").entrySet().forEach { (_, el) ->
            el.asJsonObject.apply {
                addProperty("chance", 100.0)
                addProperty("minRate", 100.0)
                addProperty("maxRate", 100.0)
            }
        }

        // New property: special.
        config.add("special", parseString("{ \"useHealSmartChance\": true, \"minPeriod\": 5, \"maxPeriod\": 15 }"))

        // System tray was added back; notify when minimised.
        config.addProperty("trayNotified", false)
    }

    /** Make sure there are no null properties. */
    private fun doLegacyMigration(config: JsonObject) {
        if (config.get("id") == null) {
            config.addProperty("id", "")
        }
        if (config.get("dotaPath") == null) {
            config.addProperty("dotaPath", "")
        }
        if (config.get("discordToken") == null) {
            config.addProperty("discordToken", "")
        }
        if (config.get("soundBoard") == null) {
            config.add("soundBoard", JsonArray())
        }
        if (config.get("modVersion") == null) {
            config.addProperty("modVersion", "")
        }
        if (config.getAsJsonObject("sounds").get("OnRespawn") == null) {
            config.getAsJsonObject("sounds").add("OnRespawn", parseString("{ \"enabled\": \"false\", \"playThroughDiscord\": false, \"sounds\": [] }"))
        }
    }
}