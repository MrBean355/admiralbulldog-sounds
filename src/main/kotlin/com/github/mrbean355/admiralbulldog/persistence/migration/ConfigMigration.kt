package com.github.mrbean355.admiralbulldog.persistence.migration

import com.github.mrbean355.admiralbulldog.persistence.CONFIG_VERSION
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonPrimitive

/** Migrate old `config.json` files to be compatible with this app version. */
object ConfigMigration {

    fun run(json: String): JsonElement {
        return JsonParser.parseString(json).asJsonObject
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
        obj.getAsJsonPrimitive("volume").let {
            // Change volume from double to int.
            obj.add("volume", JsonPrimitive(it.asDouble.toInt()))
        }
        obj.configVersion = 2
    }

    private var JsonObject.configVersion: Int?
        get() = get("version")?.asInt
        set(value) = add("version", JsonPrimitive(value))
}