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
                sounds[index] = JsonPrimitive(element.asString.toLowerCase())
            }
        }

        // Lower case Discord sound board.
        val soundBoard = config.getAsJsonArray("soundBoard")
        soundBoard.forEachIndexed { index, element ->
            soundBoard[index] = JsonPrimitive(element.asString.toLowerCase())
        }

        // Lower case custom volumes.
        val volumes = config.getAsJsonObject("volumes")
        val newVolumes = JsonObject()
        volumes?.keySet()?.forEach { sound ->
            newVolumes[sound.toLowerCase()] = volumes[sound].asInt
        }
        config["volumes"] = newVolumes
    }
}