package com.github.mrbean355.admiralbulldog.assets

import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

/** Folder that the downloaded sounds live in. */
const val SOUNDS_PATH = "sounds"
/** Special sounds that don't exist on the PlaySounds page. */
private val SPECIAL_SOUNDS = listOf("herewegoagain.mp3", "useyourmidas.wav", "wefuckinglost.wav")

object SoundFiles {
    private var allSounds = emptyList<SoundFile>()

    /** Should we check for new sounds? */
    fun shouldSync(): Boolean {
        val lastSync = ConfigPersistence.getLastSync()
        val now = System.currentTimeMillis()
        return now - lastSync >= TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS)
    }

    /**
     * Synchronise our local sounds with the PlaySounds page.
     * Downloads sounds which don't exist locally.
     * Deletes local sounds which don't exist remotely.
     */
    fun synchronise(action: (String) -> Unit, success: () -> Unit) {
        val downloaded = AtomicInteger()
        val deleted = AtomicInteger()
        GlobalScope.launch {
            val localFiles = getLocalFiles().toMutableList()
            val remoteFiles = PlaySounds.listRemoteFiles()

            /* Download all remote files that don't exist locally. */
            coroutineScope {
                remoteFiles.forEach {
                    localFiles.remove(it.fileName)
                    launch {
                        if (!it.existsLocally()) {
                            PlaySounds.downloadFile(it)
                            downloaded.incrementAndGet()
                            action("Downloaded: ${it.fileName}")
                        }
                    }
                }
            }
            /* Delete local files that don't exist remotely. */
            coroutineScope {
                launch {
                    localFiles.forEach {
                        File("${SOUNDS_PATH}/$it").delete()
                        deleted.incrementAndGet()
                        action("Deleted old sound: $it")
                    }
                    copySpecialSounds()
                }
            }

            allSounds = emptyList()
            action("Done!\n" +
                    "-> Downloaded ${downloaded.get()} new sound(s).\n" +
                    "-> Deleted ${deleted.get()} old sound(s).\n")
            withContext(Main) { success() }
        }
    }

    /** @return a list of all currently downloaded sounds. */
    fun getAll(): List<SoundFile> {
        if (allSounds.isEmpty()) {
            val root = File(SOUNDS_PATH)
            if (!root.exists() || !root.isDirectory) {
                println("Error: couldn't file sounds directory")
                return emptyList()
            }
            allSounds = root.list()?.map { SoundFile(it) }
                    .orEmpty()
        }
        return allSounds
    }

    /**
     * Find a downloaded sound by name.
     * @return the sound if found, `null` otherwise.
     */
    fun findSound(name: String): SoundFile? {
        return getAll().firstOrNull { it.name == name }
    }

    private fun getLocalFiles(): List<String> {
        val root = File(SOUNDS_PATH)
        if (!root.exists()) {
            root.mkdirs()
            return emptyList()
        }
        return root.list()?.toList().orEmpty().filter {
            it !in SPECIAL_SOUNDS
        }
    }

    private fun copySpecialSounds() {
        SPECIAL_SOUNDS.forEach {
            if (!File("$SOUNDS_PATH/$it").exists()) {
                val stream = SoundFiles::class.java.classLoader.getResourceAsStream("unmonitored/$it")
                if (stream != null) {
                    Files.copy(stream, Paths.get("sounds/$it"))
                }
            }
        }
    }
}