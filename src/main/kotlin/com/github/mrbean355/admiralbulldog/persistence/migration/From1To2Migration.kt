/*
 * Copyright 2022 Michael Johnston
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

package com.github.mrbean355.admiralbulldog.persistence.migration

import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive

class From1To2Migration : Migration(from = 1, to = 2) {

    override fun migrate(config: JsonObject) {
        // New property: bounty rune timer.
        config.getAsJsonObject("special")["bountyRuneTimer"] = 15

        // Lower case all selected sounds.
        config.getAsJsonObject("sounds").entrySet().forEach { trigger ->
            val sounds = trigger.value.asJsonObject.getAsJsonArray("sounds")
            sounds.forEachIndexed { index, element ->
                sounds[index] = JsonPrimitive(element.asString.lowercase())
            }
        }

        // Lower case Discord sound board.
        val soundBoard = config.getAsJsonArray("soundBoard")
        soundBoard.forEachIndexed { index, element ->
            soundBoard[index] = JsonPrimitive(element.asString.lowercase())
        }

        // Lower case custom volumes.
        val volumes = config.getAsJsonObject("volumes")
        val newVolumes = JsonObject()
        volumes?.keySet()?.forEach { sound ->
            newVolumes[sound.lowercase()] = volumes[sound].asInt
        }
        config["volumes"] = newVolumes
    }
}