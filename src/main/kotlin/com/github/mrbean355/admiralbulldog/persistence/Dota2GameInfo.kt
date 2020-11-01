package com.github.mrbean355.admiralbulldog.persistence

import java.io.File

private const val MOD_MARKER = "// abd-mod-marker"
private const val DOTA_REGEX = "\\s*Game\\s*dota\\s*"
private const val MOD_REGEX = "\\s*Game\\s*[\\w-]+\\s*$MOD_MARKER\\s*"

object Dota2GameInfo {

    /**
     * Update the `gameinfo.gi` file to include only these mod directories.
     */
    fun setIncludedModDirectories(directories: Collection<String>) {
        val output = StringBuilder()
        val gameInfo = File(DotaPath.getGameInfoFilePath())
        val dotaRegex = DOTA_REGEX.toRegex()
        val modRegex = MOD_REGEX.toRegex()
        var finished = false

        gameInfo.useLines { sequence ->
            sequence.forEach { line ->
                if (line.matches(dotaRegex)) {
                    directories.forEach {
                        output.appendLine("\t\t\tGame\t\t\t\t${it} $MOD_MARKER")
                    }
                    finished = true
                }
                if (finished || !line.matches(modRegex)) {
                    output.appendLine(line)
                }
            }
        }

        gameInfo.writeText(output.toString())
    }
}