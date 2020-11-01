package com.github.mrbean355.admiralbulldog.assets

import com.github.mrbean355.admiralbulldog.common.DEFAULT_INDIVIDUAL_VOLUME
import com.github.mrbean355.admiralbulldog.common.DEFAULT_RATE
import com.github.mrbean355.admiralbulldog.exception.PlaySoundErrorFile
import com.github.mrbean355.admiralbulldog.exception.writeExceptionLog
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer
import java.io.File
import java.util.concurrent.CopyOnWriteArrayList

/**
 * A downloaded sound bite which the user can choose to be played.
 */
class SoundBite(
        /** Path to the file. */
        private val filePath: String
) {
    /** Name of the file, excluding directories. */
    val fileName: String = filePath.substringAfterLast('/')

    /** Name of the file, excluding directories and the file extension. */
    val name: String = fileName.substringBeforeLast('.')

    /**
     * Play the sound bite using at the given [rate] and [volume].
     * The volume is combined with the global app volume.
     *
     * If no [rate] argument is given, plays at [DEFAULT_RATE].
     * If no [volume] argument is given:
     * - Plays at the user's chosen individual volume if applicable.
     * - Else plays at [DEFAULT_INDIVIDUAL_VOLUME].
     */
    fun play(rate: Int = DEFAULT_RATE, volume: Int = -1) {
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
                onEndOfMedia = Runnable {
                    dispose()
                    players.remove(this)
                }
                // Keep a strong reference to players until they finish.
                // Prevents sounds stopping early due to garbage collection.
                players += this
                play()
            }
        } catch (t: Throwable) {
            logError(filePath, t)
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
