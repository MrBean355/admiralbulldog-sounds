package com.github.mrbean355.dota2.integration.assets

import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer
import java.util.concurrent.atomic.AtomicBoolean

private const val VOLUME = 0.20
/* Prevent garbage collection. */
private var mediaPlayer: MediaPlayer? = null
private val initialised = AtomicBoolean(false)

interface FileName {
    val fileName: String
}

fun FileName.play() {
    if (!initialised.getAndSet(true)) {
        javafx.embed.swing.JFXPanel()
    }
    val resource = javaClass.classLoader.getResource(fileName)
    val media = Media(resource.toURI().toString())
    mediaPlayer = MediaPlayer(media).apply {
        volume = VOLUME
        play()
    }
}
