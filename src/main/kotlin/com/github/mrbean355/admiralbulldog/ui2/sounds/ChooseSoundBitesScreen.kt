package com.github.mrbean355.admiralbulldog.ui2.sounds

import com.github.mrbean355.admiralbulldog.ui.getString
import com.github.mrbean355.admiralbulldog.ui2.CheckBoxWithButtonCell
import com.github.mrbean355.admiralbulldog.ui2.Spacing
import com.github.mrbean355.admiralbulldog.ui2.triggers.SoundTrigger
import tornadofx.Fragment
import tornadofx.Scope
import tornadofx.listview
import tornadofx.paddingAll
import tornadofx.textfield
import tornadofx.vbox

class ChooseSoundBitesScreen : Fragment(getString("title_sound_bites")) {
    private val viewModel by inject<ChooseSoundBitesViewModel>(Scope(), params)

    override val root = vbox(spacing = Spacing.SMALL) {
        paddingAll = Spacing.MEDIUM
        textfield(viewModel.searchQuery) {
            promptText = getString("prompt_search")
        }
        listview(viewModel.filteredSoundBites) {
            setCellFactory {
                CheckBoxWithButtonCell(
                        onButtonClicked = { viewModel.onPlayButtonClicked(it) },
                        getSelectedProperty = { viewModel.getEnabledProperty(it) }
                )
            }
        }
    }

    companion object {
        fun params(soundTrigger: SoundTrigger): Map<*, Any?> {
            return mapOf("soundTrigger" to soundTrigger)
        }
    }
}