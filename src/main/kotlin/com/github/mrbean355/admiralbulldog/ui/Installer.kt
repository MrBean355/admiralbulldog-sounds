package com.github.mrbean355.admiralbulldog.ui

import com.github.mrbean355.admiralbulldog.HEADER_INSTALLER
import com.github.mrbean355.admiralbulldog.MSG_INSTALLER_SUCCESS
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import org.slf4j.LoggerFactory
import java.io.File

/** Path (within the root Dota directory) to the GSI directory. */
private const val GSI_DIR = "game/dota/cfg/gamestate_integration"
/** Name of the GSI file. */
private const val GSI_FILE = "gamestate_integration_bulldog.cfg"

object Installer {
    private val logger = LoggerFactory.getLogger(Installer::class.java)

    /**
     * Creates the Game State Integration file if it doesn't exists.
     * If it exists, updates its content.
     *
     * @throws IllegalStateException if the GSI file couldn't be created.
     */
    fun installIfNecessary(dotaPath: String) {
        val gsiDirectory = File("$dotaPath/$GSI_DIR".replaceFileSeparators())
        if (!gsiDirectory.exists()) {
            if (gsiDirectory.mkdirs()) {
                logger.info("Created GSI directory: ${gsiDirectory.absolutePath}")
            } else {
                throw IllegalStateException("Couldn't create GSI directory: ${gsiDirectory.absolutePath}")
            }
        }
        val gsiFile = File(gsiDirectory.absolutePath + "/$GSI_FILE".replaceFileSeparators())
        val firstTime = !gsiFile.exists()
        if (firstTime && !gsiFile.createNewFile()) {
            throw IllegalStateException("Couldn't create GSI file: ${gsiFile.absolutePath}")
        }
        gsiFile.writeText("""
            "AdmiralBulldog Sounds"
            {
                "uri"           "http://localhost:${ConfigPersistence.getPort()}"
                "timeout"       "5.0"
                "buffer"        "0.1"
                "throttle"      "0.1"
                "heartbeat"     "30.0"
                "data"
                {
                    "provider"      "1"
                    "map"           "1"
                    "player"        "1"
                    "hero"          "1"
                    "abilities"     "1"
                    "items"         "1"
                }
            }
        """.trimIndent())

        if (firstTime) {
            Alert(type = Alert.AlertType.INFORMATION,
                    header = HEADER_INSTALLER,
                    content = MSG_INSTALLER_SUCCESS,
                    buttons = arrayOf(ButtonType.FINISH)
            ).showAndWait()
        }
        logger.info("Wrote GSI file contents: ${gsiFile.absolutePath}")
    }
}
