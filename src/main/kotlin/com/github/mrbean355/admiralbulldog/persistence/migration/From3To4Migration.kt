/*
 * Copyright 2024 Michael Johnston
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

import com.google.gson.JsonArray
import com.google.gson.JsonObject

class From3To4Migration : Migration(from = 3, to = 4) {

    override fun migrate(config: JsonObject) {
        // New property: wisdom rune timer.
        config.getAsJsonObject("special")["wisdomRuneTimer"] = 45

        // New triggers: OnWisdomRunesSpawn, OnPause, RoshanTimer.
        config.getAsJsonObject("sounds").apply {
            add("OnWisdomRunesSpawn", triggerConfig())
            add("OnPause", triggerConfig())
            add("RoshanTimer", triggerConfig())
        }
    }

    private fun triggerConfig() = JsonObject().apply {
        addProperty("enabled", false)
        addProperty("chance", 100)
        addProperty("minRate", 100)
        addProperty("maxRate", 100)
        addProperty("playThroughDiscord", false)
        add("sounds", JsonArray())
    }
}