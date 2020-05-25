package com.github.mrbean355.admiralbulldog.sounds

import com.github.mrbean355.admiralbulldog.common.PADDING_MEDIUM
import com.github.mrbean355.admiralbulldog.common.PADDING_SMALL
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.events.SoundTrigger
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import javafx.scene.control.ButtonBar.ButtonData.OK_DONE
import tornadofx.Fragment
import tornadofx.action
import tornadofx.button
import tornadofx.buttonbar
import tornadofx.paddingAll
import tornadofx.vbox
import kotlin.reflect.KClass

class ChooseSoundFilesScreen : Fragment() {
    private val type by param<KClass<out SoundTrigger>>()
    private val tracker = SoundBiteTracker(ConfigPersistence.getSoundsForType(type))

    override val root = vbox(spacing = PADDING_SMALL) {
        paddingAll = PADDING_MEDIUM
        children += tracker.createSearchField()
        children += tracker.createListView()
        buttonbar {
            button(getString("btn_save"), OK_DONE) {
                action { onSaveClicked() }
            }
        }
    }

    init {
        title = type.friendlyName
    }

    private fun onSaveClicked() {
        ConfigPersistence.saveSoundsForType(type, tracker.getSelection())
        close()
    }

    companion object {

        fun params(type: KClass<out SoundTrigger>): Map<String, Any?> {
            return mapOf("type" to type)
        }
    }
}