package com.github.mrbean355.admiralbulldog.assets

import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer
import javafx.util.Duration
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
    // FIXME: toUpperCase() is here for backwards compatibility with existing config files. Make case-insensitive?
    val name: String = fileName.substringBeforeLast('.').toUpperCase()

    fun play() {
        val media = Media(File(filePath).toURI().toString())
        MediaPlayer(media).apply {
            // Without setting the start time, some sounds don't play on MacOS ¯\_(ツ)_/¯
            startTime = Duration.ZERO
            volume = ConfigPersistence.getVolume() / 100.0
            onEndOfMedia = Runnable {
                dispose()
                players.remove(this)
            }
            // Keep a strong reference to players until they finish.
            // Prevents sounds stopping early due to garbage collection.
            players += this
            play()
        }
    }

    override fun toString(): String {
        return "SoundBite(filePath='$filePath', fileName='$fileName', name='$name')"
    }

    private companion object {
        private val players = CopyOnWriteArrayList<MediaPlayer>()
    }
}
