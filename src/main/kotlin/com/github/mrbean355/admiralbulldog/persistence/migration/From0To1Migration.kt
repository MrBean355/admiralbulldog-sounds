package com.github.mrbean355.admiralbulldog.persistence.migration

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonPrimitive

class From0To1Migration : Migration(from = 0, to = 1) {

    override fun migrate(config: JsonObject) {
        doLegacyMigration(config)

        // Volume changed from double to int.
        config.getAsJsonPrimitive("volume").let {
            config.add("volume", JsonPrimitive(it.asDouble.toInt()))
        }

        // New property: updates.
        config.add("updates", JsonParser.parseString("{ \"appUpdateCheck\": 0, \"appUpdateFrequency\": \"WEEKLY\", \"soundsUpdateFrequency\": \"DAILY\", \"modUpdateCheck\": 0, \"modUpdateFrequency\": \"ALWAYS\" }"))

        // New "sounds" object properties: chance, minRate, maxRate.
        config.getAsJsonObject("sounds").entrySet().forEach { (_, el) ->
            el.asJsonObject.apply {
                addProperty("chance", 100.0)
                addProperty("minRate", 100.0)
                addProperty("maxRate", 100.0)
            }
        }

        // New property: special.
        config.add("special", JsonParser.parseString("{ \"useHealSmartChance\": true, \"minPeriod\": 5, \"maxPeriod\": 15 }"))

        // Convert sound bite names to lower case (as on the PlaySounds page).
        config.getAsJsonObject("sounds").entrySet().forEach { trigger ->
            val jsonObject = trigger.value.asJsonObject
            val newItems = JsonArray()
            jsonObject.getAsJsonArray("sounds").forEach { sound ->
                newItems.add(sound.asString.toLowerCase())
            }
            jsonObject.add("sounds", newItems)
        }

        // Same as above for the sound board.
        val newSoundBoard = JsonArray()
        config.getAsJsonArray("soundBoard").forEach { sound ->
            newSoundBoard.add(sound.asString.toLowerCase())
        }
        config.add("soundBoard", newSoundBoard)

        // System tray was added back; notify when minimised.
        config.addProperty("trayNotified", false)
    }

    /** Make sure there are no null properties. */
    private fun doLegacyMigration(obj: JsonObject) {
        if (obj.get("id") == null) {
            obj.addProperty("id", "")
        }
        if (obj.get("dotaPath") == null) {
            obj.addProperty("dotaPath", "")
        }
        if (obj.get("discordToken") == null) {
            obj.addProperty("discordToken", "")
        }
        if (obj.get("soundBoard") == null) {
            obj.add("soundBoard", JsonArray())
        }
        if (obj.get("modVersion") == null) {
            obj.addProperty("modVersion", "")
        }
    }
}