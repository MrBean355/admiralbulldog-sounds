package com.github.mrbean355.admiralbulldog.ui

import com.github.mrbean355.admiralbulldog.HEADER_INSTALLER
import com.github.mrbean355.admiralbulldog.MSG_INSTALLER
import com.github.mrbean355.admiralbulldog.TITLE_INSTALLER
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.stage.DirectoryChooser
import javafx.stage.Window
import org.slf4j.LoggerFactory
import java.io.File

private val DOTA_ROOT_DIR_NAMES = listOf("dota 2", "dota 2 beta")
private const val GAME_INFO_DIR_PATH = "game/dota/gameinfo.gi"
private const val MOD_DIR_PATH = "game/admiralbulldog"
const val VPK_FILE_NAME = "pak01_dir.vpk"

object DotaPath {
    private val logger = LoggerFactory.getLogger(DotaPath::class.java)

    /**
     * Try to retrieve the Dota 2 installation path.
     * The returned String is guaranteed to be an absolute path to the user's Dota 2 installation directory.
     *
     * If the path has not been stored in the app's config, the user will be prompted to choose their Dota 2 directory.
     *
     * @throws IllegalStateException    if the user denies the request to choose the installation path.
     * @throws IllegalArgumentException if the user chooses an invalid installation path.
     */
    fun loadPath(ownerWindow: Window): String {
        val cached = ConfigPersistence.getDotaPath()
        if (cached != null && cached == getDotaRootDirectory(cached)) {
            logger.info("Loaded Dota path from config: $cached")
            return cached
        }
        val action = Alert(
                type = Alert.AlertType.INFORMATION,
                header = HEADER_INSTALLER,
                content = MSG_INSTALLER,
                buttons = arrayOf(ButtonType.NEXT),
                owner = ownerWindow
        ).showAndWait().toNullable()

        if (action !== ButtonType.NEXT) {
            logger.error("User dismissed the alert dialog")
            throw IllegalStateException("Operation cancelled")
        }

        val result = DirectoryChooser().run {
            title = TITLE_INSTALLER
            showDialog(ownerWindow)
        }
        if (result == null) {
            logger.error("User selected no directory")
            throw IllegalArgumentException("No directory chosen")
        }

        val selectedPath = result.absolutePath
        val dotaPath = getDotaRootDirectory(selectedPath)
        if (dotaPath != null) {
            ConfigPersistence.setDotaPath(dotaPath)
            return dotaPath
        }
        logger.error("User selected an invalid Dota directory: $selectedPath")
        throw IllegalArgumentException("Invalid Dota path chosen")
    }

    /** @return absolute path to the mod directory. */
    fun getModDirectory(): String {
        val dotaPath = ConfigPersistence.getDotaPath()!!
        return dotaPath + MOD_DIR_PATH.replaceFileSeparators()
    }

    /** @return absolute path to the mod's VPK file. */
    fun getModFilePath(): String {
        return getModDirectory() + File.separatorChar + VPK_FILE_NAME
    }

    private fun getDotaRootDirectory(path: String): String? {
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