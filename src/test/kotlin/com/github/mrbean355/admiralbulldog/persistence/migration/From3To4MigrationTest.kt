/*
 * Copyright 2023 Michael Johnston
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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