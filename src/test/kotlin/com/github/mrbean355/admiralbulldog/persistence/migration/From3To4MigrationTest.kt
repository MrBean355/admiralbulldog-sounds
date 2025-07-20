package com.github.mrbean355.admiralbulldog.persistence.migration

import com.github.mrbean355.admiralbulldog.loadJsonObject
import com.google.gson.JsonObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class From3To4MigrationTest {
    private lateinit var migration: From3To4Migration

    @Before
    fun setUp() {
        migration = From3To4Migration()
    }

    @Test
    fun testConfig() {
        assertEquals(3, migration.from)
        assertEquals(4, migration.to)
    }

    @Test
    fun testMigrate() {
        val config = loadJsonObject("config-4.json")

        migration.migrate(config)

        assertEquals(45, config.getAsJsonObject("special").getAsJsonPrimitive("wisdomRuneTimer").asInt)

        config.getAsJsonObject("sounds").apply {
            getAsJsonObject("OnWisdomRunesSpawn").verifyTriggerConfig()
            getAsJsonObject("OnPause").verifyTriggerConfig()
            getAsJsonObject("RoshanTimer").verifyTriggerConfig()
        }
    }

    private fun JsonObject.verifyTriggerConfig() {
        assertEquals(6, size())
        assertFalse(getAsJsonPrimitive("enabled").asBoolean)
        assertEquals(100, getAsJsonPrimitive("chance").asInt)
        assertEquals(100, getAsJsonPrimitive("minRate").asInt)
        assertEquals(100, getAsJsonPrimitive("maxRate").asInt)
        assertFalse(getAsJsonPrimitive("playThroughDiscord").asBoolean)
        assertTrue(getAsJsonArray("sounds").isEmpty)
    }
}