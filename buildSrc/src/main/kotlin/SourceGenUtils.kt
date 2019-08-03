import org.gradle.api.Project
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

data class SoundEnum(val fileName: String, val path: String)

fun Project.generateSourceFile(packageName: String, className: String, enums: List<SoundEnum>): Int {
    val enumEntries = enums.sortedBy { it.fileName }.map {
        var enumEntry = it.fileName.substringBeforeLast('.').toUpperCase()
        if (!enumEntry.matches(ENUM_ENTRY_PATTERN)) {
            enumEntry = "`$enumEntry`"
        }
        "$enumEntry(\"${it.path}/${it.fileName}\")"
    }

    val contentBuilder = StringBuilder()
    enumEntries.forEachIndexed { index, line ->
        contentBuilder.append(line.prependIndent())
        if (index < enumEntries.size - 1) {
            contentBuilder.append(",\n")
        }
    }

    val text = """
package $packageName

@javax.annotation.Generated("DownloadSoundFilesTask", date = "${getFormattedDate()}")
@Suppress("SpellCheckingInspection", "EnumEntryName", "Unused")
enum class SoundFile(val path: String) {
$contentBuilder
}
        
    """.trimIndent()

    val destination = "src/main/kotlin/${packageName.replace('.', '/')}"
    project.file(destination).mkdirs()
    project.file("$destination/$className.kt").writeText(text)

    return text.lines().size
}

/** Regex for a valid enum entry (wraps invalid entries in backticks). */
private val ENUM_ENTRY_PATTERN = Regex("[A-Z]([A-Za-z\\d]*|[A-Z_\\d]*)")

private fun getFormattedDate(): String {
    val format = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy").apply {
        timeZone = TimeZone.getTimeZone("CAT")
    }
    return format.format(Date())
}