package com.github.mrbean355.admiralbulldog

import com.github.mrbean355.admiralbulldog.assets.SoundBite
import com.github.mrbean355.admiralbulldog.ui.Space
import com.github.mrbean355.admiralbulldog.ui.finalise
import com.github.mrbean355.admiralbulldog.ui.getString
import com.github.mrbean355.admiralbulldog.ui.showModal
import javafx.beans.InvalidationListener
import javafx.geometry.Insets
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.Tooltip
import javafx.scene.layout.FlowPane
import javafx.scene.layout.VBox
import javafx.stage.Stage

class SoundBoardStage : Stage() {
    private val viewModel = SoundBoardViewModel(this)
    private val soundsContainer = FlowPane(PADDING_SMALL, PADDING_SMALL)

    init {
        val root = VBox(PADDING_SMALL).apply {
            padding = Insets(PADDING_MEDIUM)
        }
        root.children += Label(getString("label_sound_board_description"))
        root.children += Space()
        root.children += Label(getString("label_sound_board_empty")).apply {
            visibleProperty().bind(viewModel.isEmpty)
            managedProperty().bind(visibleProperty())
        }
        root.children += soundsContainer
        root.children += Space()
        root.children += Button(ACTION_CHOOSE_SOUNDS).apply {
            setOnAction { chooseSoundsClicked() }
        }
        finalise(title = TITLE_SOUND_BOARD, root = root)
        setOnHiding {
            viewModel.onClose()
        }
        viewModel.soundBoard.addListener(InvalidationListener {
            buildSoundBoard(viewModel.soundBoard)
        })
        viewModel.init()
    }

    private fun buildSoundBoard(items: List<SoundBite>) {
        soundsContainer.children.clear()
        items.forEach { soundBite ->
            soundsContainer.children += Button(soundBite.name).apply {
                setOnAction { viewModel.onSoundClicked(soundBite) }
                tooltip = Tooltip(TOOLTIP_PLAY_ON_DISCORD)
            }
        }
    }

    private fun chooseSoundsClicked() {
        ConfigureSoundBoardStage().showModal(owner = this, wait = true)
        viewModel.refresh()
        sizeToScene()
    }
}