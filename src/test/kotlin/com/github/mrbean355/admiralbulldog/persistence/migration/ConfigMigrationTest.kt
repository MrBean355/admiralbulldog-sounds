package com.github.mrbean355.admiralbulldog.persistence.migration

import com.google.gson.JsonObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ConfigMigrationTest {

    @Test
    fun testMigrationFromUnVersionedConfig() {
        val input = loadJson("config-1.json")

        val output = ConfigMigration.run(input) as JsonObject

        assertEquals(20, output.get("volume").asInt)

        output.getAsJsonObject("updates").let {
            assertEquals(0, it.getAsJsonPrimitive("appUpdateCheck").asLong)
            assertEquals("WEEKLY", it.getAsJsonPrimitive("appUpdateFrequency").asString)
            assertEquals("DAILY", it.getAsJsonPrimitive("soundsUpdateFrequency").asString)
            assertEquals(0, it.getAsJsonPrimitive("modUpdateCheck").asLong)
            assertEquals("ALWAYS", it.getAsJsonPrimitive("modUpdateFrequency").asString)
        }

        output.getAsJsonObject("sounds").entrySet().forEach { (_, el) ->
            el.asJsonObject.apply {
                assertEquals(100.0, getAsJsonPrimitive("chance").asDouble, 0.0)
                assertEquals(100.0, getAsJsonPrimitive("minRate").asDouble, 0.0)
                assertEquals(100.0, getAsJsonPrimitive("maxRate").asDouble, 0.0)
            }
        }

        output.getAsJsonObject("special").let {
            assertTrue(it.getAsJsonPrimitive("useHealSmartChance").asBoolean)
            assertEquals(5, it.getAsJsonPrimitive("minPeriod").asInt)
            assertEquals(15, it.getAsJsonPrimitive("maxPeriod").asInt)
        }

        assertEquals(2, output.get("version").asInt)
    }

    private fun loadJson(fileName: String): String {
        return ConfigMigrationTest::class.java.classLoader.getResource(fileName)!!.readText()
    }
}