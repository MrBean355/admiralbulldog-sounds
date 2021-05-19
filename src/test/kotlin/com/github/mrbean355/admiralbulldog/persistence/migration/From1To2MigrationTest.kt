package com.github.mrbean355.admiralbulldog.persistence.migration

import com.github.mrbean355.admiralbulldog.loadJsonObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class From1To2MigrationTest {
    private lateinit var migration: From1To2Migration

    @Before
    fun setUp() {
        migration = From1To2Migration()
    }

    @Test
    fun testMigrate() {
        val config = loadJsonObject("config-2.json")

        migration.migrate(config)

        assertEquals(15, config.getAsJsonObject("special").getAsJsonPrimitive("bountyRuneTimer").asInt)

        config.getAsJsonObject("sounds").entrySet().forEach {
            it.value.asJsonObject.getAsJsonArray("sounds").forEach { sound ->
                assertTrue(sound.asString.isLowerCase())
            }
        }

        config.getAsJsonArray("soundBoard").forEach { sound ->
            assertTrue(sound.asString.isLowerCase())
        }

        config.getAsJsonObject("volumes").keySet().forEach { sound ->
            assertTrue(sound.isLowerCase())
        }
    }

    private fun String.isLowerCase(): Boolean {
        return this == lowercase()
    }
}