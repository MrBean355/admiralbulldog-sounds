import com.squareup.kotlinpoet.*
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File

private const val ICON_DIRECTORY = "src/main/resources"

open class GenerateIconsTask : DefaultTask() {

    @TaskAction
    fun run() {
        val javaxImage = ClassName("javafx.scene.image", "Image")
        val suppress = ClassName("", "Suppress")

        val icons = project.file(ICON_DIRECTORY).list().orEmpty()
                .filter { it.endsWith(".png") || it.endsWith(".jpg") }
                .sorted()

        val functions = icons.map {
            FunSpec.builder(transform(it))
                    .returns(javaxImage)
                    .addStatement("return loadImage(\"$it\")")
                    .build()
        }

        FileSpec.builder("com.github.mrbean355.admiralbulldog.common", "Icons")
                .addAnnotation(AnnotationSpec.builder(suppress)
                        .addMember("\"FunctionName\"")
                        .build())
                .addImport("com.github.mrbean355.admiralbulldog", "DotaApplication")
                .apply {
                    functions.forEach { addFunction(it) }
                }
                .addFunction(FunSpec.builder("loadImage")
                        .addModifiers(KModifier.PRIVATE)
                        .addParameter(ParameterSpec("name", STRING))
                        .returns(javaxImage)
                        .addStatement("return Image(DotaApplication::class.java.classLoader.getResourceAsStream(name))")
                        .build())
                .build()
                .writeTo(File(project.projectDir, "/src/main/kotlin"))
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