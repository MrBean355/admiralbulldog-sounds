import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

private const val BASE_TARGET_DIR = "src/main/resources/"

/**
 * Scrapes the PlaySounds web page and collects a list of sound bytes.
 * Downloads sound bytes that don't exist locally.
 * Deletes local sound bytes that don't exist remotely.
 * Generates an enum class with entries for each sound byte.
 */
open class DownloadSoundBytesTask : DefaultTask() {
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
        // Set up local directories:
        val monitoredPath = "$BASE_TARGET_DIR/$monitoredSoundsDir"
        val monitoredFile = project.file(monitoredPath)
        monitoredFile.mkdirs()

        val unmonitoredPath = "$BASE_TARGET_DIR/$unmonitoredSoundsDir"
        val unmonitoredFile = project.file(unmonitoredPath)
        unmonitoredFile.mkdirs()

        // Remember which files exist locally:
        val existingFiles = monitoredFile.list().orEmpty()

        // Download missing remote files:
        val remoteFiles = listRemoteFiles()
        remoteFiles.forEach { remoteFile ->
            if (!project.file("$monitoredPath/${remoteFile.fileName}").exists()) {
                downloadFile(remoteFile, monitoredPath)
                println("Downloaded new file: ${remoteFile.fileName}")
            }
        }

        // Delete files which don't exist remotely anymore:
        val remoteFileNames = remoteFiles.map { it.fileName }
        existingFiles.filterNot { it in remoteFileNames }
                .forEach {
                    project.file("$monitoredPath/$it").delete()
                    println("Deleted old file: $it")
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