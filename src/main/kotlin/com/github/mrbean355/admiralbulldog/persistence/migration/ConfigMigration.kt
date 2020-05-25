package com.github.mrbean355.admiralbulldog.persistence.migration

import com.github.mrbean355.admiralbulldog.persistence.CONFIG_VERSION
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

    private var JsonObject.configVersion: Int?
        get() = get("version")?.asInt
        set(value) = add("version", JsonPrimitive(value))
}