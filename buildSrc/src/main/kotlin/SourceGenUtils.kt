import org.gradle.api.Project

data class SoundEnum(val fileName: String, val path: String)

fun Project.generateSourceFile(packageName: String, className: String, enums: List<SoundEnum>): Int {
    val lines = enums.sortedBy { it.fileName }.map {
        var enumEntry = it.fileName.substringBeforeLast('.').toUpperCase()
        if (!enumEntry.matches(ENUM_ENTRY_PATTERN)) {
            enumEntry = "`$enumEntry`"
        }
        "    $enumEntry(\"${it.path}/${it.fileName}\"),\n"
    }

    val text = "package $packageName\n" +
            "\n" +
            "enum class SoundByte (override val fileName: String) : FileName {\n" +
            lines.joinToString(separator = "").removeSuffix(",\n") + "\n" +
            "}\n"

    val destination = "build/generated/kotlin/${packageName.replace('.', '/')}"
    project.file(destination).mkdirs()
    project.file("$destination/$className.kt").writeText(text)

    return text.lines().size
}

/** Regex for a valid enum entry (wraps invalid entries in backticks). */
private val ENUM_ENTRY_PATTERN = Regex("[A-Z]([A-Za-z\\d]*|[A-Z_\\d]*)")
