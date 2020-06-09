package com.github.mrbean355.admiralbulldog.persistence.migration

import com.github.mrbean355.admiralbulldog.persistence.CONFIG_VERSION
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser.parseString
import com.google.gson.JsonPrimitive
import org.slf4j.LoggerFactory

/**
 * Migrate old `config.json` files to be compatible with this app version.
 *
 * We can't serialise objects here, because those objects may have changed since that config version was introduced.
 * Instead we have to add manual JSON string representations of what the objects looked like at time of implementation.
 */
class ConfigMigration {
    private val logger = LoggerFactory.getLogger(ConfigMigration::class.java)

    fun run(json: String): JsonElement {
        val config = parseString(json).asJsonObject
        var version = config.version

        if (version == CONFIG_VERSION) {
            logger.info("Already migrated to version $version.")
            return config
        }

        check(version < CONFIG_VERSION) { "Unsupported config version: $version. Please run a later version of the app." }

        // Apply migrations until we reach the version we support.
        while (version < CONFIG_VERSION) {
            val migration = migrations.single { it.from == version }
            logger.info("Migrate from ${migration.from} to ${migration.to}")
            migration.migrate(config)
            version = migration.to
            config.version = version
        }

        // Forget about invalid sounds when upgrading.
        clearInvalidSounds(config)
        logger.info("Finished migrating to version $version.")

        return config
    }

    /** Remove sound bite entries from triggers & sound board that are in the 'invalidSounds' list. */
    private fun clearInvalidSounds(obj: JsonObject) {
        val invalidSounds = obj.getAsJsonArray("invalidSounds")?.map { it.asString }
        if (invalidSounds.isNullOrEmpty()) {
            return
        }
        obj.getAsJsonObject("sounds").entrySet().forEach { trigger ->
            trigger.value.asJsonObject.getAsJsonArray("sounds").removeAll {
                it.asString in invalidSounds
            }
        }
        obj.getAsJsonArray("soundBoard").removeAll {
            it.asString in invalidSounds
        }
    }

    private var JsonObject.version: Int
        get() = get("version")?.asInt ?: 0
        set(value) = add("version", JsonPrimitive(value))

    private val migrations: List<Migration> = listOf(
            From0To1Migration()
    )
}

abstract class Migration(val from: Int, val to: Int) {

    abstract fun migrate(config: JsonObject)

}