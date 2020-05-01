package com.github.mrbean355.admiralbulldog.ui2

import com.github.mrbean355.admiralbulldog.arch.verifyChecksum
import com.github.mrbean355.admiralbulldog.ui2.persistance.AppConfig
import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.CopyOnWriteArrayList

data class SoundBite(val fileName: String) {
    val name = fileName.substringBeforeLast('.')

    fun verifyChecksum(other: String): Boolean {
        return File(SOUNDS_DIR, fileName).verifyChecksum(other)
    }

    fun play(rate: Double = 100.0) {
        val media = Media(File(SOUNDS_DIR, fileName).toURI().toString())
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
        private val SOUNDS_DIR = File("sounds")
        private val CACHE = mutableSetOf<SoundBite>()
        private val PLAYERS = CopyOnWriteArrayList<MediaPlayer>()

        init {
            SOUNDS_DIR.mkdirs()
        }

        /** @return a collection of all sound bites on disk. */
        @Synchronized
        fun getAll(): Collection<SoundBite> {
            if (CACHE.isEmpty()) {
                CACHE += SOUNDS_DIR.listFiles().orEmpty().map {
                    SoundBite(it.name)
                }
            }
            return CACHE.toList()
        }

        /** @return `true` if a sound bite file called [fileName] exists; `false` otherwise. */
        fun doesFileExist(fileName: String): Boolean {
            val name = fileName.substringBeforeLast('.')
            return getAll().any { it.name == name }
        }

        /**
         * Save a new sound bite file called [fileName] on disk from [body].
         * @return [SoundBite] representing the saved sound bite.
         */
        @Synchronized
        fun save(fileName: String, body: ResponseBody): SoundBite {
            body.byteStream().use { input ->
                FileOutputStream(File(SOUNDS_DIR, fileName)).use { output ->
                    input.copyTo(output)
                }
            }
            return SoundBite(fileName).also {
                CACHE += it
            }
        }

        /**
         * Delete all sound bites on disk which are not in [master].
         * @return deleted sound bites.
         */
        @Synchronized
        fun deleteOthers(master: Collection<String>): Collection<String> {
            val toRemove = CACHE.filterNot { it.fileName in master }
            toRemove.forEach {
                File(SOUNDS_DIR, it.fileName).delete()
            }
            CACHE.removeAll(toRemove)
            return toRemove.map { it.name }
        }

        /** @return [SoundBite] representing the sound bite called [name]. */
        fun named(name: String): SoundBite {
            return getAll().first { it.name == name }
        }

        /** @return [SoundBite] representing the sound bite with file name [fileName]. */
        fun fileNamed(fileName: String): SoundBite {
            return named(fileName.substringBeforeLast('.'))
        }
    }
}