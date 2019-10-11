package com.github.mrbean355.admiralbulldog.assets

import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer
import javafx.util.Duration
import java.io.File
import java.util.concurrent.CopyOnWriteArrayList

/**
 * A downloaded sound file which the user can choose to be played.
 */
class SoundFile(fileName: String) {
    val name: String = fileName.substringBeforeLast('.').toUpperCase() //fixme
    val path: String = "${SOUNDS_PATH}/$fileName"

    fun play() {
        val media = Media(File(path).toURI().toString())
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
        return "SoundFile(name='$name', path='$path')"
    }

    private companion object {
        private val players = CopyOnWriteArrayList<MediaPlayer>()
    }
}
