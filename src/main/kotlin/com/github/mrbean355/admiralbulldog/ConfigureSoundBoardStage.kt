package com.github.mrbean355.admiralbulldog

import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import com.github.mrbean355.admiralbulldog.ui.SoundFileTracker
import com.github.mrbean355.admiralbulldog.ui.finalise
import javafx.geometry.Insets
import javafx.scene.control.Button
import javafx.scene.layout.VBox
import javafx.stage.Stage

class ConfigureSoundBoardStage : Stage() {
    private val tracker = SoundFileTracker(ConfigPersistence.getSoundBoard())

    init {
        val root = VBox(PADDING_SMALL)
        root.padding = Insets(PADDING_MEDIUM)
        root.children += tracker.createSearchField()
        root.children += tracker.createListView()
        root.children += Button(ACTION_SAVE).apply {
            setOnAction { saveToggles() }
        }
        finalise(title = TITLE_CONFIGURE_SOUND_BOARD, root = root)
    }

    private fun saveToggles() {
        ConfigPersistence.setSoundBoard(tracker.getSelection())
        close()
    }
}