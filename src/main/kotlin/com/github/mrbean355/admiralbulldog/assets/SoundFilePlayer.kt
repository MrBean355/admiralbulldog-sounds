package com.github.mrbean355.admiralbulldog.assets

import com.github.mrbean355.admiralbulldog.DotaApplication
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer
import javafx.util.Duration
import java.util.concurrent.CopyOnWriteArrayList

private val players = CopyOnWriteArrayList<MediaPlayer>()

fun SoundFile.playSound() {
    val resource = DotaApplication::class.java.classLoader.getResource(path)
    if (resource == null) {
        println("Can't play $this; resource not found: $path")
        return
    }
    val media = Media(resource.toURI().toString())
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