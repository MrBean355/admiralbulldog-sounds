import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

private const val ICON_DIRECTORY = "src/main/resources"
private const val OUTPUT_FILE = "src/main/kotlin/com/github/mrbean355/admiralbulldog/common/Icons.kt"

open class GenerateIconsTask : DefaultTask() {

    @TaskAction
    fun run() {
        val icons = project.file(ICON_DIRECTORY).list().orEmpty()
                .filter { it.endsWith(".png") || it.endsWith(".jpg") }
                .sorted()

        val functions = icons.joinToString(separator = "\n\n            ") {
            "fun ${transform(it)}(): Image = loadImage(\"$it\")"
        }

        project.file(OUTPUT_FILE).writeText("""
            @file:Suppress("FunctionName", "NOTHING_TO_INLINE")

            package com.github.mrbean355.admiralbulldog.common
            
            import com.github.mrbean355.admiralbulldog.DotaApplication
            import javafx.scene.image.Image
            
            $functions
            
            private fun loadImage(name: String) = Image(DotaApplication::class.java.classLoader.getResourceAsStream(name))
            
        """.trimIndent())
    }

    private fun transform(fileName: String): String = buildString {
        val name = fileName.substringBefore('.').capitalize() + "Icon"
        var i = 0
        var upper = false
        while (i < name.length) {
            if (upper) {
                upper = false
                append(name[i].toUpperCase())
            } else if (name[i] == '_') {
                upper = true
            } else {
                append(name[i])
            }
            ++i
        }
    }
}