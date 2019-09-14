import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

private const val BASE_TARGET_DIR = "src/main/resources/"

/**
 * Scrapes the PlaySounds web page and collects a list of sound files.
 * Downloads sound files that don't exist locally.
 * Deletes local sound files that don't exist remotely.
 * Generates an enum class with entries for each sound files.
 */
open class DownloadSoundFilesTask : DefaultTask() {
    /** Generated class's package name. */
    var packageName = ""
    /** Generated class's name. */
    var className = ""
    /** Directory within 'resources' to find monitored sounds. */
    var monitoredSoundsDir = ""
    /** Directory within 'resources' to find unmonitored sounds. */
    var unmonitoredSoundsDir = ""

    @TaskAction
    fun download() {
        val remoteFiles = listRemoteFiles()
        println("Found ${remoteFiles.size} remote files.")

        // Set up local directories:
        val monitoredPath = "$BASE_TARGET_DIR/$monitoredSoundsDir"
        val monitoredFile = project.file(monitoredPath)
        if (monitoredFile.exists()) {
            monitoredFile.deleteRecursively()
        }
        monitoredFile.mkdirs()

        val unmonitoredPath = "$BASE_TARGET_DIR/$unmonitoredSoundsDir"
        val unmonitoredFile = project.file(unmonitoredPath)
        unmonitoredFile.mkdirs()

        // Download all remote files:
        remoteFiles.forEach { remoteFile ->
            downloadFile(remoteFile, monitoredPath)
        }

        // Generate the enum class:
        val monitoredSounds = monitoredFile.listFiles().orEmpty().map {
            SoundEnum(it.name, monitoredSoundsDir)
        }
        val unmonitoredSounds = unmonitoredFile.listFiles().orEmpty().map {
            SoundEnum(it.name, unmonitoredSoundsDir)
        }
        val length = project.generateSourceFile(packageName, className, monitoredSounds + unmonitoredSounds)
        println("Wrote $length lines to source file: $packageName.$className")
    }
}