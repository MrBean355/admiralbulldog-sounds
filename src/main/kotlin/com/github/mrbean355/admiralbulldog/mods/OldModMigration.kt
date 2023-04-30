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

package com.github.mrbean355.admiralbulldog.mods

import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import org.slf4j.LoggerFactory
import java.io.File

object OldModMigration {
    private val logger = LoggerFactory.getLogger(OldModMigration::class.java)

    /**
     * Try to delete old mod directories.
     * It doesn't matter if it fails (e.g. Dota is running); they are unused anyway.
     */
    fun run() {
        val modDir = File(ConfigPersistence.getDotaPath(), "game")
        val toDelete = listOf(
            "base-mod",
            "custom-spell-icons",
            "custom-spell-sounds",
            "elegiggle-deny",
            "fat-mango",
            "manly-bullwhip",
            "mask-of-maldness",
            "old-hero-icons",
            "twitch-emotes",
            "yep-ping",
        )

        try {
            toDelete.forEach {
                File(modDir, it).deleteRecursively()
            }
        } catch (t: Throwable) {
            logger.error("Error deleting old mod directories", t)
        }
    }
}