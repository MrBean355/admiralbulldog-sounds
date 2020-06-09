package com.github.mrbean355.admiralbulldog

import com.github.mrbean355.admiralbulldog.persistence.migration.ConfigMigrationTest
import com.google.gson.JsonObject
import com.google.gson.JsonParser

fun loadJson(fileName: String): String {
    return ConfigMigrationTest::class.java.classLoader.getResource(fileName)!!.readText()
}

fun loadJsonObject(fileName: String): JsonObject {
    return JsonParser.parseString(loadJson(fileName)).asJsonObject
}
