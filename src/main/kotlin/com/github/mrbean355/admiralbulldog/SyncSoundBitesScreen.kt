package com.github.mrbean355.admiralbulldog

import com.github.mrbean355.admiralbulldog.assets.SoundBites
import com.github.mrbean355.admiralbulldog.common.PADDING_MEDIUM
import com.github.mrbean355.admiralbulldog.common.PADDING_SMALL
import com.github.mrbean355.admiralbulldog.common.warning
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import com.github.mrbean355.admiralbulldog.ui.getString
import javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE
import javafx.scene.control.ButtonBar.ButtonData.OK_DONE
import javafx.scene.control.ProgressBar.INDETERMINATE_PROGRESS
import tornadofx.Fragment
import tornadofx.action
import tornadofx.booleanProperty
import tornadofx.button
import tornadofx.buttonbar
import tornadofx.doubleProperty
import tornadofx.enableWhen
import tornadofx.fitToParentWidth
import tornadofx.onChange
import tornadofx.paddingAll
import tornadofx.progressbar
import tornadofx.runLater
import tornadofx.stringProperty
import tornadofx.textarea
import tornadofx.vbox

class SyncSoundBitesScreen : Fragment(getString("sync_sound_bites_title")) {
    private val progress = doubleProperty(INDETERMINATE_PROGRESS)
    private val output = stringProperty(getString("sync_sound_bites_welcome"))
    private val finished = booleanProperty(false)

    override val root = vbox(spacing = PADDING_SMALL) {
        paddingAll = PADDING_MEDIUM
        progressbar(progress) {
            fitToParentWidth()
        }
        textarea(output) {
            isEditable = false
            output.onChange {
                // Auto-scroll to the bottom
                selectEnd()
                deselect()
            }
        }
        buttonbar {
            button(getString("btn_cancel"), type = CANCEL_CLOSE) {
                action { close() }
            }
            button(getString("btn_done"), type = OK_DONE) {
                enableWhen(finished)
                action { close() }
            }
        }
    }

    init {
        SoundBites.synchronise(action = {
            runLater {
                output.value = "${output.value}\n$it"
            }
        }, progress = {
            progress.set(it)
        }, complete = { successful ->
            if (successful) {
                ConfigPersistence.markLastSync()
            }
            finished.set(true)
        })
        finished.onChange {
            ConfigPersistence.getInvalidSounds().also {
                if (it.isNotEmpty()) {
                    warning(getString("header_sounds_removed"), getString("msg_sounds_removed", it.joinToString()))
                }
            }
            ConfigPersistence.clearInvalidSounds()
        }
    }
}
