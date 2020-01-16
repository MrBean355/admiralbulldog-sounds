package com.github.mrbean355.admiralbulldog

import com.github.mrbean355.admiralbulldog.assets.SoundByte
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import com.github.mrbean355.admiralbulldog.service.playSoundOnDiscord
import com.github.mrbean355.admiralbulldog.ui.Alert
import com.github.mrbean355.admiralbulldog.ui.finalise
import com.github.mrbean355.admiralbulldog.ui.showModal
import javafx.geometry.Insets
import javafx.scene.control.Alert
import javafx.scene.control.Button
import javafx.scene.control.ButtonType
import javafx.scene.control.Label
import javafx.scene.control.Tooltip
import javafx.scene.layout.FlowPane
import javafx.scene.layout.VBox
import javafx.stage.Stage
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

    private fun soundClicked(soundByte: SoundByte) {
        GlobalScope.launch {
            if (!playSoundOnDiscord(soundByte)) {
                withContext(Main) {
                    Alert(type = Alert.AlertType.ERROR,
                            header = HEADER_DISCORD_SOUND,
                            content = MSG_DISCORD_PLAY_FAILED.format(soundByte.name),
                            buttons = arrayOf(ButtonType.OK),
                            owner = this@SoundBoardStage
                    ).show()
                }
            }
        }
    }
}