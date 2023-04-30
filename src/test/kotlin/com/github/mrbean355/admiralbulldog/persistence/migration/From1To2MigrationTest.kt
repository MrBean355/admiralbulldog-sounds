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
        return none(Char::isUpperCase)
    }
}