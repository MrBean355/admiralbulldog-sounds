/*
 * Copyright 2022 Michael Johnston
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

package com.github.mrbean355.admiralbulldog.persistence.migration

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser.parseString

class From0To1Migration : Migration(from = 0, to = 1) {

    override fun migrate(config: JsonObject) {
        doLegacyMigration(config)

        // Volume changed from double to int.
        config.getAsJsonPrimitive("volume").let {
            config["volume"] = it.asDouble.toInt()
        }

        // New property: updates.
        config["updates"] = parseString("{ \"appUpdateCheck\": 0, \"appUpdateFrequency\": \"WEEKLY\", \"soundsUpdateFrequency\": \"DAILY\", \"modUpdateCheck\": 0, \"modUpdateFrequency\": \"ALWAYS\" }")

        // New "sounds" object properties: chance, minRate, maxRate.
        config.getAsJsonObject("sounds").entrySet().forEach { (_, el) ->
            el.asJsonObject.apply {
                this["chance"] = 100.0
                this["minRate"] = 100.0
                this["maxRate"] = 100.0
            }
        }

        // New property: special.
        config["special"] = parseString("{ \"useHealSmartChance\": true, \"minPeriod\": 5, \"maxPeriod\": 15 }")

        // System tray was added back; notify when minimised.
        config["trayNotified"] = false
    }

    /** Make sure there are no null properties. */
    private fun doLegacyMigration(config: JsonObject) {
        if (config.get("id") == null) {
            config["id"] = ""
        }
        if (config.get("dotaPath") == null) {
            config["dotaPath"] = ""
        }
        if (config.get("discordToken") == null) {
            config["discordToken"] = ""
        }
        if (config.get("soundBoard") == null) {
            config["soundBoard"] = JsonArray()
        }
        if (config.get("modVersion") == null) {
            config["modVersion"] = ""
        }
        if (config.getAsJsonObject("sounds").get("OnRespawn") == null) {
            config.getAsJsonObject("sounds")["OnRespawn"] = parseString("{ \"enabled\": \"false\", \"playThroughDiscord\": false, \"sounds\": [] }")
        }
    }
}