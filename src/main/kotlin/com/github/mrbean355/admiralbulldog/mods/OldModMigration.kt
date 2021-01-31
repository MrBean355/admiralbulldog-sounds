/*
 * Copyright 2021 Michael Johnston
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

package com.github.mrbean355.admiralbulldog.mods

import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import com.github.mrbean355.admiralbulldog.persistence.DotaPath
import org.slf4j.LoggerFactory
import java.io.File

object OldModMigration {

    fun run() {
        // Delete VPK directory:
        try {
            File(ConfigPersistence.getDotaPath(), "game/admiralbulldog").deleteRecursively()
        } catch (t: Throwable) {
            LoggerFactory.getLogger(OldModMigration::class.java).error("Error deleting old mod VPK", t)
        }
        // Remove from gameinfo.gi file:
        try {
            val gameInfoFile = File(DotaPath.getGameInfoFilePath())
            val regex = "^\\s*Game\\s*admiralbulldog\\s*$".toRegex()
            val content = buildString {
                gameInfoFile.useLines {
                    it.forEach { line ->
                        if (!line.matches(regex)) {
                            appendLine(line)
                        }
                    }
                }
            }
            gameInfoFile.writeText(content)
        } catch (t: Throwable) {
            LoggerFactory.getLogger(OldModMigration::class.java).error("Error updating game info file", t)
        }
    }
}