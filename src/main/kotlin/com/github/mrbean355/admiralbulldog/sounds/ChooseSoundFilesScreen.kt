package com.github.mrbean355.admiralbulldog.sounds

import com.github.mrbean355.admiralbulldog.common.PADDING_MEDIUM
import com.github.mrbean355.admiralbulldog.common.PADDING_SMALL
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.events.SoundTriggerType
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import javafx.scene.control.ButtonBar.ButtonData.OK_DONE
import tornadofx.Fragment
import tornadofx.action
import tornadofx.button
import tornadofx.buttonbar
import tornadofx.paddingAll
import tornadofx.vbox

class ChooseSoundFilesScreen : Fragment() {
    private val type by param<SoundTriggerType>()
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

        fun params(type: SoundTriggerType): Map<String, Any?> {
            return mapOf("type" to type)
        }
    }
}