/*
 * Copyright 2022 Michael Johnston
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.mrbean355.admiralbulldog.persistence.migration

import com.github.mrbean355.admiralbulldog.loadJsonObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class From0To1MigrationTest {
    private lateinit var migration: From0To1Migration

    @Before
    fun setUp() {
        migration = From0To1Migration()
    }

    @Test
    fun testMigrationFromLegacyConfig() {
        val config = loadJsonObject("config-legacy.json")

        migration.migrate(config)

        // Convert to non-nullable properties:
        assertEquals("", config.getAsJsonPrimitive("id").asString)
        assertEquals("", config.getAsJsonPrimitive("dotaPath").asString)
        assertEquals("", config.getAsJsonPrimitive("discordToken").asString)
        assertEquals(0, config.getAsJsonArray("soundBoard").size())
        assertEquals("", config.getAsJsonPrimitive("modVersion").asString)
        assertNotNull(config.getAsJsonObject("sounds").get("OnRespawn"))
    }

    @Test
    fun testMigrationFromUnVersionedConfig() {
        val config = loadJsonObject("config-1.json")

        migration.migrate(config)

        assertEquals(20, config.get("volume").asInt)

        config.getAsJsonObject("updates").let {
            assertEquals(0, it.getAsJsonPrimitive("appUpdateCheck").asLong)
            assertEquals("WEEKLY", it.getAsJsonPrimitive("appUpdateFrequency").asString)
            assertEquals("DAILY", it.getAsJsonPrimitive("soundsUpdateFrequency").asString)
            assertEquals(0, it.getAsJsonPrimitive("modUpdateCheck").asLong)
            assertEquals("ALWAYS", it.getAsJsonPrimitive("modUpdateFrequency").asString)
        }

        config.getAsJsonObject("sounds").entrySet().forEach { (_, el) ->
            el.asJsonObject.apply {
                assertEquals(100.0, getAsJsonPrimitive("chance").asDouble, 0.0)
                assertEquals(100.0, getAsJsonPrimitive("minRate").asDouble, 0.0)
                assertEquals(100.0, getAsJsonPrimitive("maxRate").asDouble, 0.0)
            }
        }

        config.getAsJsonObject("special").let {
            assertTrue(it.getAsJsonPrimitive("useHealSmartChance").asBoolean)
            assertEquals(5, it.getAsJsonPrimitive("minPeriod").asInt)
            assertEquals(15, it.getAsJsonPrimitive("maxPeriod").asInt)
        }

        assertFalse(config.getAsJsonPrimitive("trayNotified").asBoolean)
    }
}