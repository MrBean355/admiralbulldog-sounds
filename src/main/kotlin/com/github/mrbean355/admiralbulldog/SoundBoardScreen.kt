package com.github.mrbean355.admiralbulldog

import com.github.mrbean355.admiralbulldog.assets.SoundBite
import com.github.mrbean355.admiralbulldog.ui.getString
import com.github.mrbean355.admiralbulldog.ui.showModal
import javafx.collections.ObservableList
import javafx.event.EventTarget
import javafx.scene.control.ButtonBar.ButtonData.NEXT_FORWARD
import javafx.scene.control.Tooltip
import javafx.scene.layout.FlowPane
import tornadofx.View
import tornadofx.action
import tornadofx.attachTo
import tornadofx.button
import tornadofx.buttonbar
import tornadofx.label
import tornadofx.managedWhen
import tornadofx.onChange
import tornadofx.paddingAll
import tornadofx.spacer
import tornadofx.vbox
import tornadofx.visibleWhen
import tornadofx.whenUndocked

class SoundBoardScreen : View(getString("title_sound_board")) {
    private val viewModel by inject<SoundBoardViewModel>()

    override val root = vbox(spacing = PADDING_SMALL) {
        paddingAll = PADDING_MEDIUM
        label(getString("label_sound_board_description"))
        label(getString("label_sound_board_empty")) {
            visibleWhen(viewModel.isEmpty)
            managedWhen(visibleProperty())
        }
        soundBoard(viewModel.soundBoard, viewModel::onSoundClicked)
        spacer {
            prefHeight = PADDING_SMALL
        }
        buttonbar {
            button(getString("action_choose_sounds"), NEXT_FORWARD) {
                action { onChooseSoundsClicked() }
            }
        }
    }

    init {
        whenUndocked {
            viewModel.onUndock()
        }
    }

    private fun onChooseSoundsClicked() {
        ConfigureSoundBoardStage().showModal(wait = true)
        viewModel.refresh()
    }
}

private fun EventTarget.soundBoard(items: ObservableList<SoundBite>, onClick: (SoundBite) -> Unit): FlowPane {
    return FlowPane(PADDING_SMALL, PADDING_SMALL).apply {
        addButtons(items, onClick)
        items.onChange {
            addButtons(items, onClick)
        }
    }.attachTo(this)
}

private fun FlowPane.addButtons(items: ObservableList<SoundBite>, onClick: (SoundBite) -> Unit) {
    children.clear()
    items.forEach {
        button(it.name) {
            tooltip = Tooltip(getString("tooltip_play_through_discord"))
            action { onClick(it) }
        }
    }
    scene?.window?.sizeToScene()
}
