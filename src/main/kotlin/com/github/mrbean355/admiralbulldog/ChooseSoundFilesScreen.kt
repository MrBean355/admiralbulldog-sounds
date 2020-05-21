package com.github.mrbean355.admiralbulldog

import com.github.mrbean355.admiralbulldog.events.SoundEvent
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import com.github.mrbean355.admiralbulldog.ui.SoundBiteTracker
import com.github.mrbean355.admiralbulldog.ui.getString
import javafx.scene.control.ButtonBar.ButtonData.OK_DONE
import tornadofx.Fragment
import tornadofx.action
import tornadofx.button
import tornadofx.buttonbar
import tornadofx.paddingAll
import tornadofx.vbox
import kotlin.reflect.KClass

class ChooseSoundFilesScreen : Fragment() {
    private val type by param<KClass<out SoundEvent>>()
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

        fun params(type: KClass<out SoundEvent>): Map<String, Any?> {
            return mapOf("type" to type)
        }
    }
}