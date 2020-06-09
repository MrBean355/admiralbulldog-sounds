package com.github.mrbean355.admiralbulldog.home

import com.github.mrbean355.admiralbulldog.common.PADDING_MEDIUM
import com.github.mrbean355.admiralbulldog.common.PADDING_SMALL
import com.github.mrbean355.admiralbulldog.common.SettingsIcon
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.common.useLargeFont
import com.github.mrbean355.admiralbulldog.common.useSmallFont
import com.github.mrbean355.admiralbulldog.settings.SettingsScreen
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.geometry.Pos.CENTER
import javafx.scene.image.ImageView
import tornadofx.View
import tornadofx.action
import tornadofx.button
import tornadofx.fitToParentWidth
import tornadofx.hbox
import tornadofx.hyperlink
import tornadofx.insets
import tornadofx.label
import tornadofx.managedWhen
import tornadofx.paddingAll
import tornadofx.progressbar
import tornadofx.separator
import tornadofx.stackpane
import tornadofx.stackpaneConstraints
import tornadofx.tooltip
import tornadofx.vbox
import tornadofx.visibleWhen
import tornadofx.whenUndocked

class MainScreen : View(getString("title_app")) {
    private val viewModel by inject<MainViewModel>()

    override val root = stackpane {
        vbox(spacing = PADDING_SMALL, alignment = CENTER) {
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
                hyperlink(getString("btn_telegram_channel")) {
                    action { viewModel.onTelegramChannelClicked() }
                }
            }
            label(viewModel.version) {
                useSmallFont()
            }
        }
        button(graphic = ImageView(SettingsIcon())) {
            tooltip(getString("tooltip_settings"))
            action {
                find<SettingsScreen>().openModal(resizable = false)
            }
            stackpaneConstraints {
                alignment = Pos.BOTTOM_RIGHT
                margin = insets(PADDING_SMALL)
            }
        }
    }

    init {
        currentStage?.isResizable = false
        whenUndocked {
            viewModel.onUndock()
        }
    }
}
