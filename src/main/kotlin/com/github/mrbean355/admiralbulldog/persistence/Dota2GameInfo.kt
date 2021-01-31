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