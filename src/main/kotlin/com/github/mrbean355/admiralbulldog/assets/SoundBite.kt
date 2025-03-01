/*
 * Copyright 2024 Michael Johnston
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.mrbean355.admiralbulldog.assets

import com.github.mrbean355.admiralbulldog.common.DEFAULT_INDIVIDUAL_VOLUME
import com.github.mrbean355.admiralbulldog.common.DEFAULT_RATE
import com.github.mrbean355.admiralbulldog.exception.PlaySoundErrorFile
import com.github.mrbean355.admiralbulldog.exception.writeExceptionLog
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer
import javafx.util.Duration
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import java.io.File
import java.util.concurrent.CopyOnWriteArrayList

sealed class SoundBite {
    abstract val name: String

    abstract fun play(rate: Int = DEFAULT_RATE, volume: Int = -1, onComplete: () -> Unit = {})
}

class ComboSoundBite(override val name: String) : SoundBite() {

    override fun play(rate: Int, volume: Int, onComplete: () -> Unit) {
        GlobalScope.launch {
            getSoundBites().playAll(rate)
            onComplete()
        }
    }

    fun getSoundBites(): List<SoundBite> {
        return ConfigPersistence.findSoundCombo(name)
    }
}

suspend fun Iterable<SoundBite>.playAll(rate: Int = DEFAULT_RATE) {
    val mutex = Mutex()
    forEach {
        mutex.lock()
        it.play(rate = rate) {
            mutex.unlock()
        }
    }
}

/**
 * A downloaded sound bite which the user can choose to be played.
 */
class SingleSoundBite(
    /** Path to the file. */
    private val filePath: String
) : SoundBite() {
    /** Name of the file, excluding directories. */
    val fileName: String = filePath.substringAfterLast('/')

    /** Name of the file, excluding directories and the file extension. */
    override val name: String = fileName.substringBeforeLast('.')

    /**
     * Play the sound bite using at the given [rate] and [volume].
     * The volume is combined with the global app volume.
     *
     * If no [rate] argument is given, plays at [DEFAULT_RATE].
     * If no [volume] argument is given:
     * - Plays at the user's chosen individual volume if applicable.
     * - Else plays at [DEFAULT_INDIVIDUAL_VOLUME].
     */
    override fun play(rate: Int, volume: Int, onComplete: () -> Unit) {
        try {
            val media = Media(File(filePath).toURI().toString())
            MediaPlayer(media).apply {
                val individualVolume = if (volume == -1) {
                    ConfigPersistence.getSoundBiteVolume(name) ?: DEFAULT_INDIVIDUAL_VOLUME
                } else {
                    volume
                }
                this.volume = (ConfigPersistence.getVolume() / 100.0) * (individualVolume / 100.0)
                this.rate = rate / 100.0
                // Most sounds don't play on Mac without explicitly setting the start time. Strange.
                this.startTime = Duration.ZERO

                onEndOfMedia = Runnable {
                    dispose()
                    players.remove(this)
                    onComplete()
                }
                // Keep a strong reference to players until they finish.
                // Prevents sounds stopping early due to garbage collection.
                players += this
                play()
            }
        } catch (t: Throwable) {
            logError(filePath, t)
            onComplete()
        }
    }

    override fun toString(): String {
        return "SoundBite(filePath='$filePath', fileName='$fileName', name='$name')"
    }

    private companion object {
        private val players = CopyOnWriteArrayList<MediaPlayer>()

        private fun logError(filePath: String, t: Throwable) {
            synchronized(this) {
                t.writeExceptionLog(PlaySoundErrorFile, message = "Couldn't play '$filePath'")
            }
        }
    }
}
