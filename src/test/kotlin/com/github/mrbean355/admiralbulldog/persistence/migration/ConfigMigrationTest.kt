package com.github.mrbean355.admiralbulldog.persistence.migration

import com.github.mrbean355.admiralbulldog.loadJson
import com.github.mrbean355.admiralbulldog.persistence.CONFIG_VERSION
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ConfigMigrationTest {
    private lateinit var migration: ConfigMigration

    @Before
    fun setUp() {
        migration = ConfigMigration()
    }

    @Test
    fun testMigrationFromLegacyVersion_FinishesOnLatestVersion() {
        val json = loadJson("config-legacy.json")

        val actual = migration.run(json)

        assertEquals(CONFIG_VERSION, actual.asJsonObject.get("version").asInt)
    }
}