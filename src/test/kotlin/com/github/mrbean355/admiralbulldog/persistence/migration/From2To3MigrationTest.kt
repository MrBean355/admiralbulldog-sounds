package com.github.mrbean355.admiralbulldog.persistence.migration

import com.github.mrbean355.admiralbulldog.loadJsonObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit

class From2To3MigrationTest {
    private lateinit var migration: From2To3Migration

    @Before
    fun setUp() {
        migration = From2To3Migration()
    }

    @Test
    fun testConfig() {
        assertEquals(2, migration.from)
        assertEquals(3, migration.to)
    }

    @Test
    fun testMigrate() {
        val config = loadJsonObject("config-3.json")

        migration.migrate(config)

        assertFalse(config.has("feedbackCompleted"))
        val expected = 1612810650499 + TimeUnit.DAYS.toMillis(90)
        assertEquals(expected, config["nextFeedback"].asJsonPrimitive.asLong)
    }
}