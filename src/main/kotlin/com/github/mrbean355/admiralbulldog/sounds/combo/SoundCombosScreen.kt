package com.github.mrbean355.admiralbulldog.sounds.combo

import com.github.mrbean355.admiralbulldog.common.*
import javafx.scene.image.ImageView
import tornadofx.*

class SoundCombosScreen : Fragment(getString("title_sound_combos")) {
    private val viewModel by inject<SoundCombosViewModel>(Scope())

    override val root = vbox(spacing = PADDING_SMALL) {
        paddingAll = PADDING_MEDIUM
        listview(viewModel.items) {
            viewModel.selection.bind(selectionModel.selectedItemProperty())
            useLabelWithButton(
                    buttonImage = PlayIcon(),
                    buttonTooltip = getString("tooltip_play_locally"),
                    stringConverter = { it },
                    onButtonClicked = { name, _ -> viewModel.onPlayClicked(name) }
            )
            onDoubleClick {
                viewModel.onListDoubleClicked()
            }
        }
        hbox(spacing = PADDING_SMALL) {
            button(graphic = ImageView(HelpIcon())) {
                action { viewModel.onHelpClicked() }
            }
            spacer()
            button(graphic = ImageView(DeleteIcon())) {
                enableWhen(viewModel.hasSelection)
                action { viewModel.onRemoveClicked() }
            }
            button(graphic = ImageView(AddIcon())) {
                action { viewModel.onAddClicked() }
            }
        }
    }
}