import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File

open class GenerateIconsTask : DefaultTask() {

    @get:InputDirectory
    val input: File
        get() = project.file("src/jvmMain/resources")

    @get:OutputFile
    val output: File
        get() = project.file("src/jvmMain/kotlin/com/github/mrbean355/admiralbulldog/common/Icons.kt")

    @TaskAction
    fun run() {
        val icons = input.list().orEmpty()
            .filter { it.endsWith(".png") || it.endsWith(".jpg") }
            .sorted()

        val functions = icons.joinToString(separator = "\n\n") {
            """
            @Composable
            fun ${transform(it)}Painter(): Painter = painterResource("$it")
            """.trimIndent()
        }

        output.writeText(
            """
            |@file:Suppress("FunctionName")
            |
            |package com.github.mrbean355.admiralbulldog.common
            |
            |import androidx.compose.runtime.Composable
            |import androidx.compose.ui.graphics.painter.Painter
            |import androidx.compose.ui.res.painterResource
            |
            |$functions
            |""".trimMargin()
        )
    }

    private fun transform(fileName: String): String = buildString {
        val name = fileName.substringBefore('.').replaceFirstChar(Char::uppercase) + "Icon"
        var i = 0
        var upper = false
        while (i < name.length) {
            if (upper) {
                upper = false
                append(name[i].uppercase())
            } else if (name[i] == '_') {
                upper = true
            } else {
                append(name[i])
            }
            ++i
        }
    }
}