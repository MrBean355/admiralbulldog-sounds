package com.github.mrbean355.admiralbulldog.persistence.migration

import com.github.mrbean355.admiralbulldog.persistence.CONFIG_VERSION
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser.parseString
import com.google.gson.JsonPrimitive

/**
 * Migrate old `config.json` files to be compatible with this app version.
 *
 * We can't serialise objects here, because those objects may have changed since that config version was introduced.
 * Instead we have to add manual JSON string representations of what the objects looked like at time of implementation.
 */
object ConfigMigration {

    fun run(json: String): JsonElement {
        return parseString(json).asJsonObject
                .also { migrate(it) }
    }

    private fun migrate(obj: JsonObject) {
        when (obj.configVersion) {
            CONFIG_VERSION -> return
            null -> version1(obj)
            else -> throw RuntimeException("Unexpected config version: ${obj.configVersion}")
        }
        migrate(obj)
    }

    private fun version1(obj: JsonObject) {
        legacy(obj)

        // Volume changed from double to int.
        obj.getAsJsonPrimitive("volume").let {
            obj.add("volume", JsonPrimitive(it.asDouble.toInt()))
        }

        // New property: updates
        obj.add("updates", parseString("{ \"appUpdateCheck\": 0, \"appUpdateFrequency\": \"WEEKLY\", \"soundsUpdateFrequency\": \"DAILY\", \"modUpdateCheck\": 0, \"modUpdateFrequency\": \"ALWAYS\" }"))

        // New "sounds" object properties: chance, minRate, maxRate
        obj.getAsJsonObject("sounds").entrySet().forEach { (_, el) ->
            el.asJsonObject.apply {
                addProperty("chance", 100.0)
                addProperty("minRate", 100.0)
                addProperty("maxRate", 100.0)
            }
        }

        // New property: special
        obj.add("special", parseString("{ \"useHealSmartChance\": true, \"minPeriod\": 5, \"maxPeriod\": 15 }"))

        // Done
        obj.configVersion = 2
    }

    private fun legacy(obj: JsonObject) {
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

        // Convert sound bit names to lower case (as on the PlaySounds page).
        obj.getAsJsonObject("sounds").entrySet().forEach { trigger ->
            val jsonObject = trigger.value.asJsonObject
            val newItems = JsonArray()
            jsonObject.getAsJsonArray("sounds").forEach { sound ->
                newItems.add(sound.asString.toLowerCase())
            }
            jsonObject.add("sounds", newItems)
        }

        // Same as above for the sound board.
        val newSoundBoard = JsonArray()
        obj.getAsJsonArray("soundBoard").forEach { sound ->
            newSoundBoard.add(sound.asString.toLowerCase())
        }
        obj.add("soundBoard", newSoundBoard)
    }

    private var JsonObject.configVersion: Int?
        get() = get("version")?.asInt
        set(value) = add("version", JsonPrimitive(value))
}