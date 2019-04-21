package com.github.mrbean355.dota2.integration.download

import java.io.File

/** Generate enum values for downloaded sounds. */
class EnumGenerator(private vararg val names: String) {

    fun run() {
        val values = names.flatMap { generateValues(it) }
        val builder = StringBuilder()
        values.sorted().forEach {
            builder.append(it).append(",\n")
        }
        builder.delete(builder.length - 2, builder.length)
        builder.append(';')
        println(builder)
    }

    private fun generateValues(folder: String): List<String> {
        val dir = File("${AssetDownloader.DEST_DIR}/$folder/")
        val values = mutableListOf<String>()
        dir.listFiles().forEach {
            val name = it.name.replace(".${AssetDownloader.FILE_TYPE}", "").toUpperCase()
            values += "$name(\"sounds/$folder/${it.name}\")"
        }
        return values
    }
}