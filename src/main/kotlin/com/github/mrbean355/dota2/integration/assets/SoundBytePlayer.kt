package com.github.mrbean355.dota2.integration.assets

import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicBoolean

private const val VOLUME = 0.20
private val initialised = AtomicBoolean(false)
private val players = CopyOnWriteArrayList<MediaPlayer>()

interface FileName {
    val fileName: String
}

fun FileName.play() {
    if (!initialised.getAndSet(true)) {
        javafx.embed.swing.JFXPanel()
    }
    val resource = javaClass.classLoader.getResource(fileName)
    if (resource == null) {
        println("!! Error playing: $fileName")
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
