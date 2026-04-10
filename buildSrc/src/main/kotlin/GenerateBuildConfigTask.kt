import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.util.Calendar

open class GenerateBuildConfigTask : DefaultTask() {
    @get:Input
    var platform: String = ""

    @get:Input
    val version: Any
        get() = project.version

    @get:OutputFile
    val output: File
        get() = project.file("src/jvmMain/kotlin/com/github/mrbean355/admiralbulldog/BuildConfig.kt")

    @TaskAction
    fun run() {
        output.writeText(
            """
            package com.github.mrbean355.admiralbulldog
            
            import com.vdurmont.semver4j.Semver
            
            val APP_VERSION: Semver = Semver("$version")
            
            const val DISTRIBUTION: String = "$platform"
            
            """.trimIndent()
        )
    }
}