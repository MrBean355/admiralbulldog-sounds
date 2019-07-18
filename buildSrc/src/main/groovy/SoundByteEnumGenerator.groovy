import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * Generates an enum class containing a value for each downloaded sound byte.
 */
class SoundByteEnumGenerator extends DefaultTask {
    /** Types of audio files we support. */
    private static final SUPPORTED_FILE_TYPES = [".mp3", ".wav"]
    /** Regex for a valid enum entry (wraps invalid entries in backticks). */
    private static final ENUM_ENTRY_PATTERN = "[A-Z]([A-Za-z\\d]*|[A-Z_\\d]*)"

    /** Generated class's package name. */
    String packageName
    /** Generated class's name. */
    String className
    /** Directory within 'resources' to find monitored sounds. */
    String monitoredSoundsDir
    /** Directory within 'resources' to find unmonitored sounds. */
    String unmonitoredSoundsDir

    @TaskAction
    void action() {
        if (packageName == null || packageName.isEmpty()) {
            throw new IllegalArgumentException("packageName property not set")
        }
        if (className == null || className.isEmpty()) {
            throw new IllegalArgumentException("className property not set")
        }
        if (monitoredSoundsDir == null) {
            throw new IllegalArgumentException("soundsDir property not set")
        }
        final monitoredSounds = project.file("src/main/resources/$monitoredSoundsDir")
        if (!monitoredSounds.exists() || !monitoredSounds.isDirectory()) {
            throw new IllegalArgumentException("monitoredSoundsDir is not a directory: ${monitoredSounds.absolutePath}")
        }

        final files = monitoredSounds.list().toList().collect {
            new Sound(monitoredSoundsDir, it)
        }
        if (unmonitoredSoundsDir != null) {
            final unmonitoredSounds = project.file("src/main/resources/$unmonitoredSoundsDir")
            if (!unmonitoredSounds.exists() || !unmonitoredSounds.isDirectory()) {
                throw new IllegalArgumentException("unmonitoredSounds is not a directory: ${unmonitoredSounds.absolutePath}")
            }
            files.addAll(unmonitoredSounds.list().collect {
                new Sound(unmonitoredSoundsDir, it)
            })
        }
        final supported = files.findAll { isSupported(it) }
        generateSourceFile(supported)
    }

    void generateSourceFile(List<Sound> sounds) {
        def output = "package $packageName\n" +
                "\n" +
                "enum class $className(override val fileName: String) : FileName {\n"
        sounds.sort { it.fileName } .each {
            String name = it.fileName.toUpperCase().replaceAll("\\.(.+)\$", "")
            if (!name.matches(ENUM_ENTRY_PATTERN)) {
                name = "`$name`"
            }
            output += "    $name(\"${it.dirName}/${it.fileName}\"),\n"
        }
        output += "}\n"

        final path = packageName.replaceAll("\\.", "/")
        final outDir = project.file("build/generated/kotlin/$path")
        outDir.mkdirs()

        final gen = project.file("${outDir.path}/${className}.kt")
        gen.createNewFile()
        gen.setText(output)
    }

    static boolean isSupported(final Sound sound) {
        def found = false
        SUPPORTED_FILE_TYPES.forEach {
            if (sound.fileName.endsWith(it)) {
                found = true
            }
        }
        return found
    }

    class Sound {
        final String fileName
        final String dirName

        Sound(String dirName, String fileName) {
            this.fileName = fileName
            this.dirName = dirName
        }
    }
}
