/*
 * Copyright 2024 Michael Johnston
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

package com.github.mrbean355.admiralbulldog.persistence

import com.github.mrbean355.admiralbulldog.common.replaceFileSeparators
import org.slf4j.LoggerFactory
import java.io.File

/** Path (within the root Dota directory) to the GSI directory. */
private const val GSI_DIR = "game/dota/cfg/gamestate_integration"

/** Name of the GSI file. */
private const val GSI_FILE = "gamestate_integration_bulldog.cfg"

object GameStateIntegration {
    private val logger = LoggerFactory.getLogger(GameStateIntegration::class.java)

    /** @return `true` if the GSI file has already been created. */
    fun isInstalled(): Boolean = getGsiFile().exists()

    /**
     * Creates the Game State Integration file if it doesn't exist.
     * If it exists, updates its content.
     *
     * @throws IllegalStateException if the GSI file couldn't be created.
     */
    fun install() {
        val gsiFile = getGsiFile()
        if (!gsiFile.exists() && !gsiFile.createNewFile()) {
            throw IllegalStateException("Couldn't create GSI file: ${gsiFile.absolutePath}")
        }
        gsiFile.writeText(
            """
            "AdmiralBulldog Sounds"
            {
                "uri"           "http://localhost:${ConfigPersistence.getPort()}"
                "timeout"       "5.0"
                "buffer"        "0.0"
                "throttle"      "0.5"
                "heartbeat"     "30.0"
                "data"
                {
                    "provider"      "0"
                    "map"           "1"
                    "player"        "1"
                    "hero"          "1"
                    "abilities"     "0"
                    "items"         "1"
                    "buildings"     "0"
                    "draft"         "0"
                    "wearables"     "0"
                    "events"        "1"
                }
            }
            """.trimIndent()
        )

        logger.info("Wrote GSI file contents: ${gsiFile.absolutePath}")
    }

    private fun getGsiFile(): File {
        check(DotaPath.hasValidSavedPath()) { "Invalid Dota path saved" }
        val dotaPath = ConfigPersistence.getDotaPath()
        val gsiDirectory = File("$dotaPath/$GSI_DIR".replaceFileSeparators())
        if (!gsiDirectory.exists()) {
            if (gsiDirectory.mkdirs()) {
                logger.info("Created GSI directory: ${gsiDirectory.absolutePath}")
            } else {
                throw IllegalStateException("Couldn't create GSI directory: ${gsiDirectory.absolutePath}")
            }
        }
        return File(gsiDirectory.absolutePath + "/$GSI_FILE".replaceFileSeparators())
    }
}
