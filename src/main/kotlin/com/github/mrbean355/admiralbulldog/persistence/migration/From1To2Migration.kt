package com.github.mrbean355.admiralbulldog.persistence.migration

import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive

class From1To2Migration : Migration(from = 1, to = 2) {

    override fun migrate(config: JsonObject) {
        config.getAsJsonObject("special").add("bountyRuneTimer", JsonPrimitive(15))
    }
}