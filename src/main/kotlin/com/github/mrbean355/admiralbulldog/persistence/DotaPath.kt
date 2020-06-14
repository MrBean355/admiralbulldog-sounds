package com.github.mrbean355.admiralbulldog.persistence

import com.github.mrbean355.admiralbulldog.common.replaceFileSeparators
import java.io.File

private val DOTA_ROOT_DIR_NAMES = listOf("dota 2", "dota 2 beta")
private const val GAME_INFO_DIR_PATH = "game/dota/gameinfo.gi"
private const val MOD_DIR_PATH = "game/admiralbulldog"
const val VPK_FILE_NAME = "pak01_dir.vpk"

/**
 * Utility for Dota path related things.
 */
object DotaPath {

    /** @return `true` if the saved Dota path still points to a valid Dota installation. */
    fun hasValidSavedPath(): Boolean {
        val saved = ConfigPersistence.getDotaPath()
        return saved == getDotaRootDirectory(saved)
    }

    /** @return absolute path to the gameinfo.gi file. */
    fun getGameInfoFilePath(): String {
        val dotaPath = ConfigPersistence.getDotaPath()
        return dotaPath + GAME_INFO_DIR_PATH.replaceFileSeparators()
    }

    /** @return absolute path to the mod directory. */
    fun getModDirectory(): String {
        val dotaPath = ConfigPersistence.getDotaPath()
        return dotaPath + MOD_DIR_PATH.replaceFileSeparators()
    }

    /** @return absolute path to the mod's VPK file. */
    fun getModFilePath(): String {
        return getModDirectory() + File.separatorChar + VPK_FILE_NAME
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