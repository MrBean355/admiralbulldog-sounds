package com.github.mrbean355.admiralbulldog.ui2.sounds

import com.github.mrbean355.admiralbulldog.arch.DiscordBotService
import com.github.mrbean355.admiralbulldog.ui.getString
import com.github.mrbean355.admiralbulldog.ui2.SoundBite
import javafx.beans.binding.StringBinding
import javafx.beans.property.BooleanProperty
import javafx.beans.property.DoubleProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.control.TreeItem
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tornadofx.stringBinding
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicInteger

class SyncSoundBitesViewModel {
    private val newSounds = CopyOnWriteArrayList<SoundBite>()
    private val modifiedSounds = CopyOnWriteArrayList<SoundBite>()
    private val deletedSounds = CopyOnWriteArrayList<String>()
    private val failedSounds = CopyOnWriteArrayList<String>()

    val progress: DoubleProperty = SimpleDoubleProperty()
    val complete: BooleanProperty = SimpleBooleanProperty(false)
    val header: StringBinding = complete.stringBinding {
        if (it == true) getString("label_sound_bites_updated") else getString("label_sound_bites_updating")
    }
    val tree: ObjectProperty<TreeItem<SoundBiteTreeCell.Model>> = SimpleObjectProperty(TreeItem())
    val error: ObjectProperty<Any> = SimpleObjectProperty()

    init {
        sync()
    }

    fun onRetryClicked() {
        sync()
    }

    private fun sync() {
        GlobalScope.launch {
            val response = DiscordBotService.INSTANCE.listSoundBites()
            val body = response.body()
            if (!response.isSuccessful || body == null) {
                withContext(Main) {
                    error.set(Any())
                }
                return@launch
            }
            val total = body.size.toDouble()
            val current = AtomicInteger(0)
            coroutineScope {
                body.forEach { (soundBite, checksum) ->
                    launch {
                        val exists = SoundBite.doesFileExist(soundBite)
                        if (!exists || !SoundBite.fileNamed(soundBite).verifyChecksum(checksum)) {
                            val downloadResponse = DiscordBotService.INSTANCE.downloadSoundBite(soundBite)
                            val downloadBody = downloadResponse.body()
                            if (downloadResponse.isSuccessful && downloadBody != null) {
                                val saved = SoundBite.save(soundBite, downloadBody)
                                if (exists) {
                                    modifiedSounds += saved
                                } else {
                                    newSounds += saved
                                }
                            } else {
                                failedSounds += soundBite
                            }
                        }
                        current.incrementAndGet()
                        withContext(Main) {
                            progress.set(current.get() / total)
                        }
                    }
                }
            }
            deletedSounds += SoundBite.deleteOthers(body.keys)
            populateTree()
            withContext(Main) {
                complete.set(true)
            }
        }
    }

    private suspend fun populateTree() {
        val root = TreeItem<SoundBiteTreeCell.Model>()

        newSounds.intoTree(getString("label_new_sound_bites"))?.let {
            root.children += it
        }
        modifiedSounds.intoTree(getString("label_modified_sound_bites"))?.let {
            root.children += it
        }
        deletedSounds.intoTree(getString("label_old_sound_bites"))?.let {
            root.children += it
        }
        failedSounds.intoTree(getString("label_failed_sound_bites"))?.let {
            root.children += it
        }
        withContext(Main) {
            tree.set(root)
        }
    }

    @JvmName("intoStringTree")
    private fun List<String>.intoTree(label: String): TreeItem<SoundBiteTreeCell.Model>? {
        if (isEmpty()) {
            return null
        }
        return TreeItem(SoundBiteTreeCell.Model("$label ($size)")).also { item ->
            item.children += sorted().map { TreeItem(SoundBiteTreeCell.Model(it)) }
        }
    }

    @JvmName("intoSoundBiteTree")
    private fun List<SoundBite>.intoTree(label: String): TreeItem<SoundBiteTreeCell.Model>? {
        if (isEmpty()) {
            return null
        }
        return TreeItem(SoundBiteTreeCell.Model("$label ($size)")).also { item ->
            item.children += sortedBy { it.name }.map {
                TreeItem(SoundBiteTreeCell.Model(it.name, soundBite = it) { clicked ->
                    clicked.play()
                })
            }
        }
    }
}
