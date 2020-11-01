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