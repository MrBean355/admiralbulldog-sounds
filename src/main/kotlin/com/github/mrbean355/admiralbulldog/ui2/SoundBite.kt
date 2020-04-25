package com.github.mrbean355.admiralbulldog.ui2

import com.github.mrbean355.admiralbulldog.ui2.persistance.AppConfig
import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer
import java.io.File
import java.util.concurrent.CopyOnWriteArrayList

data class SoundBite(val filePath: String) {
    val name = filePath
            .substringAfterLast(File.separatorChar)
            .substringBeforeLast('.')

    fun play(rate: Double = 100.0) {
        val media = Media(File(filePath).toURI().toString())
        PLAYERS += MediaPlayer(media).apply {
            this.volume = AppConfig.volumeProperty().value / 100.0
            this.rate = rate / 100.0
            onEndOfMedia = Runnable {
                dispose()
                PLAYERS.remove(this)
            }
            play()
        }
    }

    companion object {
        private const val SOUNDS_DIR = "sounds"
        private val CACHE = mutableListOf<SoundBite>()
        private val PLAYERS = CopyOnWriteArrayList<MediaPlayer>()

        fun getAll(): Collection<SoundBite> {
            if (CACHE.isEmpty()) {
                CACHE += File(SOUNDS_DIR).listFiles().orEmpty().map {
                    SoundBite(SOUNDS_DIR + File.separator + it.name)
                }
            }
            return CACHE.toList()
        }
    }
}