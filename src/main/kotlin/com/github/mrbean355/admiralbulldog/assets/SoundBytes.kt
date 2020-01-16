package com.github.mrbean355.admiralbulldog.assets

import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

/** Directory that the downloaded sounds live in. */
private const val SOUNDS_PATH = "sounds"
/** Directory within resources where special sounds live. */
private const val SPECIAL_SOUNDS_PATH = "unmonitored"
/** Special sounds that don't exist on the PlaySounds page. */
private val SPECIAL_SOUNDS = listOf("herewegoagain.mp3", "useyourmidas.wav", "wefuckinglost.wav")
/** How often (in milliseconds) to check for new sounds. */
private val SYNC_PERIOD = TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS)

/**
 * Synchronises our local sounds with the PlaySounds page.
 */
object SoundBytes {
    private val logger = LoggerFactory.getLogger(SoundBytes::class.java)
    private var allSounds = emptyList<SoundByte>()

    /** Should we check for new sounds? */
    fun shouldSync(): Boolean {
        val lastSync = ConfigPersistence.getLastSync()
        return System.currentTimeMillis() - lastSync >= SYNC_PERIOD
    }

    /**
     * Synchronise our local sounds with the PlaySounds page.
     * Downloads sounds which don't exist locally.
     * Deletes local sounds which don't exist remotely.
     * Copies over the [SPECIAL_SOUNDS].
     */
    fun synchronise(action: (String) -> Unit, complete: (Boolean) -> Unit) {
        val downloaded = AtomicInteger()
        val deleted = AtomicInteger()
        GlobalScope.launch {
            val localFiles = getLocalFiles().toMutableList()
            val response = PlaySounds.listRemoteSoundBytes()
            val remoteFiles = response.body
            if (!response.success || remoteFiles == null) {
                withContext(Main) { complete(false) }
                return@launch
            }

            /* Download all remote files that don't exist locally. */
            coroutineScope {
                remoteFiles.forEach {
                    localFiles.remove(it.fileName)
                    launch {
                        if (!it.existsLocally()) {
                            PlaySounds.downloadSoundByte(it, SOUNDS_PATH)
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
            withContext(Main) { complete(true) }
        }
    }

    /** @return a list of all currently downloaded sounds. */
    fun getAll(): List<SoundByte> {
        if (allSounds.isEmpty()) {
            val root = File(SOUNDS_PATH)
            if (!root.exists() || !root.isDirectory) {
                logger.error("Couldn't find sounds directory")
                return emptyList()
            }
            allSounds = root.list()?.map { SoundByte("$SOUNDS_PATH/$it") }
                    .orEmpty()
        }
        return allSounds
    }

    /**
     * Find a downloaded sound by name.
     * @return the sound if found, `null` otherwise.
     */
    fun findSound(name: String): SoundByte? {
        return getAll().firstOrNull { it.name == name }
    }

    private fun PlaySounds.RemoteSoundByte.existsLocally(): Boolean {
        return File("$SOUNDS_PATH/$fileName").exists()
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
                val stream = SoundBytes::class.java.classLoader.getResourceAsStream("$SPECIAL_SOUNDS_PATH/$it")
                if (stream != null) {
                    Files.copy(stream, Paths.get("$SOUNDS_PATH/$it"))
                }
            }
        }
    }
}