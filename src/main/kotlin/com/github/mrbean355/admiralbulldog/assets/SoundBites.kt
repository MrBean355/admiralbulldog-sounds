package com.github.mrbean355.admiralbulldog.assets

import com.github.mrbean355.admiralbulldog.arch.repo.DiscordBotRepository
import com.github.mrbean355.admiralbulldog.arch.verifyChecksum
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.common.warning
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import java.io.File
import java.util.concurrent.CopyOnWriteArrayList

/** Directory that the downloaded sounds live in. */
private const val SOUNDS_PATH = "sounds"

/**
 * Synchronises our local sounds with the PlaySounds page.
 */
object SoundBites {
    private val logger = LoggerFactory.getLogger(SoundBites::class.java)
    private val playSoundsRepository = DiscordBotRepository()
    private var allSounds = emptyList<SoundBite>()

    class SyncResult(
            val newSounds: Collection<String>,
            val changedSounds: Collection<String>,
            val deletedSounds: Collection<String>,
            val failedSounds: Collection<String>
    )

    /**
     * Synchronise our local sounds with the PlaySounds page.
     * Downloads sounds which don't exist locally.
     * Deletes local sounds which don't exist remotely.
     *
     * @return [SyncResult] with all the affected sound bites if successful; `null` if unsuccessful.
     */
    suspend fun synchronise(progress: (Double) -> Unit): SyncResult? = withContext(IO) {
        val response = playSoundsRepository.listSoundBites()
        val remoteFiles = response.body

        if (!response.isSuccessful() || remoteFiles == null) {
            logger.error("Failed to list sound bites: $response")
            return@withContext null
        }

        val newSounds = CopyOnWriteArrayList<String>()
        val changedSounds = CopyOnWriteArrayList<String>()
        val deletedSounds = CopyOnWriteArrayList<String>()
        val failedSounds = CopyOnWriteArrayList<String>()

        val filesToDelete = getLocalFiles().filter { it !in remoteFiles.keys }
        val total = remoteFiles.size.toDouble()
        var current = 0

        /* Download all remote files that don't exist locally. */
        coroutineScope {
            remoteFiles.keys.forEach { soundBite ->
                launch {
                    val existsLocally = soundBiteExistsLocally(soundBite)
                    val latestChecksum = remoteFiles.getValue(soundBite)
                    if (!existsLocally || !File("$SOUNDS_PATH/$soundBite").verifyChecksum(latestChecksum)) {
                        val soundBiteResponse = playSoundsRepository.downloadSoundBite(soundBite, SOUNDS_PATH)
                        if (soundBiteResponse.isSuccessful()) {
                            if (existsLocally) {
                                changedSounds += soundBite
                            } else {
                                newSounds += soundBite
                            }
                        } else {
                            logger.error("Failed to download: $soundBite; statusCode=${soundBiteResponse.statusCode}")
                            failedSounds += soundBite
                        }
                    }
                    withContext(Main) {
                        progress(++current / total)
                    }
                }
            }
        }

        /* Delete local files that don't exist remotely. */
        filesToDelete.forEach {
            File("${SOUNDS_PATH}/$it").delete()
            deletedSounds += it
        }

        ConfigPersistence.removeInvalidSounds(getAll().map { it.name })

        allSounds = emptyList()
        SyncResult(newSounds, changedSounds, deletedSounds, failedSounds)
    }

    /** @return a list of all currently downloaded sounds. */
    fun getAll(): List<SoundBite> {
        if (allSounds.isEmpty()) {
            val root = File(SOUNDS_PATH)
            if (!root.exists() || !root.isDirectory) {
                logger.error("Couldn't find sounds directory")
                return emptyList()
            }
            allSounds = root.list()?.map { SoundBite("$SOUNDS_PATH/$it") }
                    .orEmpty()
        }
        return allSounds
    }

    /**
     * Find a downloaded sound by name.
     * @return the sound if found, `null` otherwise.
     */
    fun findSound(name: String): SoundBite? {
        return getAll().firstOrNull { it.name.equals(name, ignoreCase = true) }
    }

    /** Show a warning message if the user selected sounds that don't exist locally. */
    fun checkForInvalidSounds() {
        ConfigPersistence.findInvalidSounds().also {
            if (it.isNotEmpty()) {
                warning(getString("header_sounds_removed"), getString("msg_sounds_removed", it.joinToString()))
            }
        }
    }

    private fun soundBiteExistsLocally(name: String): Boolean {
        return File("$SOUNDS_PATH/$name").exists()
    }

    private fun getLocalFiles(): List<String> {
        val root = File(SOUNDS_PATH)
        if (!root.exists()) {
            root.mkdirs()
            return emptyList()
        }
        return root.list()?.toList().orEmpty()
    }
}