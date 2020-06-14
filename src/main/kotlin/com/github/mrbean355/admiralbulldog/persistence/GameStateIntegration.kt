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
     * Creates the Game State Integration file if it doesn't exists.
     * If it exists, updates its content.
     *
     * @throws IllegalStateException if the GSI file couldn't be created.
     */
    fun install() {
        val gsiFile = getGsiFile()
        if (!gsiFile.exists() && !gsiFile.createNewFile()) {
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
