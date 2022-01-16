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

package com.github.mrbean355.admiralbulldog.persistence

import com.github.mrbean355.admiralbulldog.common.replaceFileSeparators
import java.io.File

private val DOTA_ROOT_DIR_NAMES = listOf("dota 2", "dota 2 beta")
private const val GAME_INFO_DIR_PATH = "game/dota/gameinfo.gi"

/**
 * Utility for Dota path related things.
 */
object DotaPath {

    /** @return `true` if the saved Dota path still points to a valid Dota installation. */
    fun hasValidSavedPath(): Boolean {
        val saved = ConfigPersistence.getDotaPath()
        return saved == getDotaRootDirectory(saved)
    }

    fun getDotaRootDirectory(path: String): String? {
        val parts = path.replaceFileSeparators().split(File.separatorChar)
        val dotaRoot = parts.firstOrNull { it in DOTA_ROOT_DIR_NAMES } ?: return null
        val dotaPath = path.replaceFileSeparators().substringBefore(dotaRoot) + dotaRoot + File.separatorChar
        val gameInfoFilePath = dotaPath + GAME_INFO_DIR_PATH.replaceFileSeparators()
        return if (File(gameInfoFilePath).exists()) {
            dotaPath
        } else {
            null
        }
    }
}