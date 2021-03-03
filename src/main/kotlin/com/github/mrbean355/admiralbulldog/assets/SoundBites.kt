/*
 * Copyright 2021 Michael Johnston
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.mrbean355.admiralbulldog.assets

import com.github.mrbean355.admiralbulldog.arch.repo.DiscordBotRepository
import com.github.mrbean355.admiralbulldog.arch.verifyChecksum
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.common.showWarning
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import javafx.scene.control.ProgressBar
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import java.io.File
import java.util.concurrent.CopyOnWriteArrayList

/** Directory that the downloaded sounds live in. */
private const val SOUNDS_PATH = "sounds"

/**
 * Works with the list of sound bite files.
 */
object SoundBites {
    private val logger = LoggerFactory.getLogger(SoundBites::class.java)
    private val playSoundsRepository = DiscordBotRepository()
    private var soundFiles = emptyList<SingleSoundBite>()
    private var soundCombos = emptyList<ComboSoundBite>()

    private val _progress = MutableStateFlow(0.0)
    val progress: StateFlow<Double> = _progress

    class SyncResult(
        val newSounds: Collection<String>,
        val changedSounds: Collection<String>,
        val deletedSounds: Collection<String>,
        val failedSounds: Collection<String>
    )

    /**
     * Synchronise our local sound files with the server.
     * Downloads files which don't exist locally.
     * Re-downloads files which are different remotely.
     * Deletes local files which don't exist remotely.
     *
     * @return [SyncResult] with all the affected sound bites if successful; `null` if unsuccessful.
     */
    suspend fun synchronise(): SyncResult? = withContext(IO) {
        _progress.value = ProgressBar.INDETERMINATE_PROGRESS
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
                    _progress.value = ++current / total
                }
            }
        }

        /* Delete local files that don't exist remotely. */
        filesToDelete.forEach {
            File("${SOUNDS_PATH}/$it").delete()
            deletedSounds += it
        }

        ConfigPersistence.removeInvalidSounds(getAll().map { it.name })

        soundFiles = emptyList()
        SyncResult(newSounds, changedSounds, deletedSounds, failedSounds)
    }

    /** @return a list of all currently downloaded sounds. */
    fun getSingleSoundBites(): List<SingleSoundBite> {
        if (soundFiles.isEmpty()) {
            val root = File(SOUNDS_PATH)
            if (!root.exists() || !root.isDirectory) {
                logger.error("Couldn't find sounds directory")
                return emptyList()
            }
            soundFiles = root.list()?.map { SingleSoundBite("$SOUNDS_PATH/$it") }.orEmpty()
        }
        return soundFiles
    }

    /** @return a list of all created sound combos. */
    fun getComboSoundBites(): List<ComboSoundBite> {
        return synchronized(this) {
            if (soundCombos.isEmpty()) {
                soundCombos = ConfigPersistence.getSoundCombos().map(::ComboSoundBite)
            }
            soundCombos
        }
    }

    /** @return a list of all currently downloaded sounds and created combos. */
    fun getAll(): List<SoundBite> {
        return getSingleSoundBites() + getComboSoundBites()
    }

    fun refreshCombos() {
        synchronized(this) {
            soundCombos = emptyList()
        }
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
                showWarning(getString("header_sounds_removed"), getString("content_sounds_removed", it.joinToString()))
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