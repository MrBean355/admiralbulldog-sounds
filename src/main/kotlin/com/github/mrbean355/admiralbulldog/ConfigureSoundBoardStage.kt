package com.github.mrbean355.admiralbulldog

import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import com.github.mrbean355.admiralbulldog.ui.SoundBiteTracker
import com.github.mrbean355.admiralbulldog.ui.getString
import javafx.scene.control.ButtonBar.ButtonData.OK_DONE
import tornadofx.View
import tornadofx.action
import tornadofx.button
import tornadofx.buttonbar
import tornadofx.paddingAll
import tornadofx.vbox

class ConfigureSoundBoardStage : View(getString("title_customise_sound_board")) {
    private val tracker = SoundBiteTracker(ConfigPersistence.getSoundBoard())

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

    private fun onSaveClicked() {
        ConfigPersistence.setSoundBoard(tracker.getSelection())
        close()
    }
}