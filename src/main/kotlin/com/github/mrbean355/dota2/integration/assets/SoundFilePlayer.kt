package com.github.mrbean355.dota2.integration.assets

import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer
import java.util.concurrent.CopyOnWriteArrayList

private const val VOLUME = 0.20
private val players = CopyOnWriteArrayList<MediaPlayer>()

fun SoundFile.play() {
    val resource = javaClass.classLoader.getResource(path)
    if (resource == null) {
        println("!! Error playing: $path")
        return
    }
    val media = Media(resource.toURI().toString())
    MediaPlayer(media).apply {
        volume = VOLUME
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
