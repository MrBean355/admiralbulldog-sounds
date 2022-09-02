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