package com.github.mrbean355.admiralbulldog.persistence.migration

import com.google.gson.JsonObject
import org.junit.Assert.assertEquals
import org.junit.Test

class ConfigMigrationTest {

    @Test
    fun testMigrationFromUnVersionedConfig() {
        val input = loadJson("config-1.json")

        val output = ConfigMigration.run(input) as JsonObject

        assertEquals(2, output.get("version").asInt)
    }

    private fun loadJson(fileName: String): String {
        return ConfigMigrationTest::class.java.classLoader.getResource(fileName)!!.readText()
    }
}