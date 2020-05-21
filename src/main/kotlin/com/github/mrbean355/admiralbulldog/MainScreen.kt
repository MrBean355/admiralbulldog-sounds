package com.github.mrbean355.admiralbulldog

import com.github.mrbean355.admiralbulldog.common.PADDING_MEDIUM
import com.github.mrbean355.admiralbulldog.common.PADDING_SMALL
import com.github.mrbean355.admiralbulldog.common.useLargeFont
import com.github.mrbean355.admiralbulldog.common.useSmallFont
import com.github.mrbean355.admiralbulldog.ui.getString
import javafx.geometry.Orientation
import javafx.geometry.Pos.CENTER
import tornadofx.View
import tornadofx.action
import tornadofx.button
import tornadofx.fitToParentWidth
import tornadofx.hbox
import tornadofx.hyperlink
import tornadofx.label
import tornadofx.managedWhen
import tornadofx.paddingAll
import tornadofx.progressbar
import tornadofx.separator
import tornadofx.vbox
import tornadofx.visibleWhen
import tornadofx.whenUndocked

class MainScreen : View(getString("title_app")) {
    private val viewModel by inject<MainViewModel>()

    override val root = vbox(spacing = PADDING_SMALL, alignment = CENTER) {
        paddingAll = PADDING_MEDIUM
        label(viewModel.heading) {
            useLargeFont()
        }
        progressbar {
            fitToParentWidth()
            visibleWhen(viewModel.progressBarVisible)
            managedWhen(visibleProperty())
        }
        label(viewModel.infoMessage)
        hbox(spacing = PADDING_SMALL, alignment = CENTER) {
            button(getString("btn_change_sounds")) {
                action { viewModel.onChangeSoundsClicked() }
            }
            button(getString("btn_discord_bot")) {
                action { viewModel.onDiscordBotClicked() }
            }
            button(getString("btn_dota_mod")) {
                action { viewModel.onDotaModClicked() }
            }
        }
        hbox(spacing = PADDING_SMALL, alignment = CENTER) {
            hyperlink(getString("btn_discord_community")) {
                action { viewModel.onDiscordCommunityClicked() }
            }
            separator(Orientation.VERTICAL)
            hyperlink(getString("btn_project_website")) {
                action { viewModel.onProjectWebsiteClicked() }
            }
        }
        label(viewModel.version) {
            useSmallFont()
        }
    }

    init {
        whenUndocked {
            viewModel.onUndock()
        }
    }
}
