import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * Generates an enum class containing a value for each downloaded sound byte.
 */
class SoundByteEnumGenerator extends DefaultTask {
    String packageName
    String className
    String soundsDir

    @TaskAction
    void action() {
        if (packageName == null || packageName.isEmpty()) {
            throw new IllegalArgumentException("packageName property not set")
        }
        if (className == null || className.isEmpty()) {
            throw new IllegalArgumentException("className property not set")
        }
        if (soundsDir == null) {
            throw new IllegalArgumentException("soundsDir property not set")
        }
        final resources = project.file("src/main/resources/$soundsDir")
        if (!resources.exists() || !resources.isDirectory()) {
            throw new IllegalArgumentException("soundsDir is not a directory: ${resources.absolutePath}")
        }

        final sounds = resources.listFiles(new FilenameFilter() {
            @Override
            boolean accept(File dir, String name) {
                return name.endsWith(".mp3")
            }
        }).collect { it.name.replace(".mp3", "") }
                .sort()

        def output = "package $packageName\n" +
                "\n" +
                "enum class $className(override val fileName: String) : FileName {\n"
        sounds.each {
            String name = it.toUpperCase()
            if (Character.isDigit(name.charAt(0))) {
                name = "_$name"
            }
            output += "    $name(\"$soundsDir/${it}.mp3\"),\n"
        }
        output += "}\n"

        final gen = project.file("src/main/kotlin/${packageName.replaceAll("\\.", "/")}/${className}.kt")
        gen.createNewFile()
        gen.setText(output)
    }
}
