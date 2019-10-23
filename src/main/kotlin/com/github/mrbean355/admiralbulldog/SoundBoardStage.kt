package com.github.mrbean355.admiralbulldog

import com.github.mrbean355.admiralbulldog.assets.SoundFile
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import com.github.mrbean355.admiralbulldog.service.playSoundOnDiscord
import com.github.mrbean355.admiralbulldog.ui.finalise
import com.github.mrbean355.admiralbulldog.ui.showModal
import javafx.geometry.Insets
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.Tooltip
import javafx.scene.layout.FlowPane
import javafx.scene.layout.VBox
import javafx.stage.Stage

class SoundBoardStage : Stage() {
    private val soundsContainer = FlowPane(PADDING_SMALL, PADDING_SMALL)

    init {
        val root = VBox(PADDING_SMALL).apply {
            padding = Insets(PADDING_MEDIUM)
        }
        root.children += soundsContainer
        root.children += Label("")
        root.children += Button(ACTION_CHOOSE_SOUNDS).apply {
            setOnAction { chooseSoundsClicked() }
        }
        buildSoundButtons()
        finalise(title = TITLE_SOUND_BOARD, root = root)
    }

    private fun buildSoundButtons() {
        soundsContainer.children.clear()
        ConfigPersistence.getSoundBoard().forEach { soundFile ->
            soundsContainer.children += Button(soundFile.name).apply {
                setOnAction { soundClicked(soundFile) }
                tooltip = Tooltip(TOOLTIP_PLAY_ON_DISCORD)
            }
        }
    }

    private fun chooseSoundsClicked() {
        ConfigureSoundBoardStage().showModal(owner = this, wait = true)
        buildSoundButtons()
        sizeToScene()
    }

    private fun soundClicked(soundFile: SoundFile) {
        playSoundOnDiscord(soundFile)
    }
}