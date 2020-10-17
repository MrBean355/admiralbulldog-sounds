import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier.CONST
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STRING
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File

open class GenerateBuildConfigTask : DefaultTask() {
    var platform: String = ""

    @TaskAction
    fun run() {
        val semver = ClassName("com.vdurmont.semver4j", "Semver")

        FileSpec.builder("com.github.mrbean355.admiralbulldog", "BuildConfig")
                .addProperty(PropertySpec.builder("APP_VERSION", semver)
                        .initializer("Semver(\"${project.version}\")")
                        .build())
                .addProperty(PropertySpec.builder("DISTRIBUTION", STRING, CONST)
                        .initializer("\"$platform\"")
                        .build())
                .build()
                .writeTo(File(project.projectDir, "/src/main/kotlin"))
    }
}