package com.github.mrbean355.admiralbulldog.discord

import com.github.mrbean355.admiralbulldog.common.PADDING_MEDIUM
import com.github.mrbean355.admiralbulldog.common.PADDING_SMALL
import com.github.mrbean355.admiralbulldog.common.URL_DISCORD_BOT_INVITE
import com.github.mrbean355.admiralbulldog.common.URL_DISCORD_WIKI
import com.github.mrbean355.admiralbulldog.events.SOUND_EVENT_TYPES
import com.github.mrbean355.admiralbulldog.sounds.friendlyName
import com.github.mrbean355.admiralbulldog.ui.getString
import javafx.scene.control.ButtonBar.ButtonData.HELP
import javafx.scene.control.ButtonBar.ButtonData.NEXT_FORWARD
import tornadofx.Fragment
import tornadofx.action
import tornadofx.button
import tornadofx.buttonbar
import tornadofx.checkbox
import tornadofx.enableWhen
import tornadofx.gridpane
import tornadofx.gridpaneConstraints
import tornadofx.hbox
import tornadofx.hyperlink
import tornadofx.imageview
import tornadofx.label
import tornadofx.paddingAll
import tornadofx.textfield
import tornadofx.textflow
import tornadofx.vbox
import tornadofx.whenUndocked

class DiscordBotScreen : Fragment(getString("title_discord_bot")) {
    private val viewModel by inject<DiscordBotViewModel>()

    override val root = vbox(spacing = PADDING_SMALL) {
        paddingAll = PADDING_MEDIUM
        textflow {
            checkbox(getString("label_enable_discord_bot"), viewModel.botEnabled)
            hyperlink(getString("label_invite_discord_bot")) {
                action { onInviteClicked() }
            }
        }
        textfield(viewModel.token) {
            promptText = getString("prompt_magic_number")
            enableWhen(viewModel.botEnabled)
        }
        hbox(spacing = PADDING_SMALL) {
            imageview(viewModel.statusImage)
            label(viewModel.status)
        }
        label(getString("label_play_through_discord"))
        gridpane {
            hgap = PADDING_SMALL
            vgap = PADDING_SMALL
            var row = 0
            var col = 0
            SOUND_EVENT_TYPES.forEach { type ->
                checkbox(type.friendlyName, viewModel.throughDiscordProperty(type)) {
                    enableWhen(viewModel.botEnabled)
                    gridpaneConstraints {
                        columnRowIndex(col++, row)
                    }
                }
                if (col == 3) {
                    col = 0
                    ++row
                }
            }
        }
        buttonbar {
            button(getString("action_sound_board"), NEXT_FORWARD) {
                enableWhen(viewModel.botEnabled)
                action { onSoundBoardClicked() }
            }
            button(getString("btn_help"), HELP) {
                action { onHelpClicked() }
            }
        }
    }

    init {
        whenUndocked {
            viewModel.onUndock()
        }
    }

    private fun onInviteClicked() {
        hostServices.showDocument(URL_DISCORD_BOT_INVITE)
    }

    private fun onHelpClicked() {
        hostServices.showDocument(URL_DISCORD_WIKI)
    }

    private fun onSoundBoardClicked() {
        find<SoundBoardScreen>().openModal(resizable = false)
    }
}