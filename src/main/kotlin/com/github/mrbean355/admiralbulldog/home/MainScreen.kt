package com.github.mrbean355.admiralbulldog.home

import com.github.mrbean355.admiralbulldog.AppStyles
import com.github.mrbean355.admiralbulldog.common.*
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import com.github.mrbean355.admiralbulldog.settings.SettingsScreen
import javafx.geometry.Pos
import javafx.geometry.Pos.CENTER
import javafx.scene.image.ImageView
import tornadofx.*

class MainScreen : View(getString("title_app")) {
    private val viewModel by inject<MainViewModel>()

    override val root = stackpane {
        vbox(spacing = PADDING_SMALL, alignment = CENTER) {
            paddingAll = PADDING_MEDIUM
            imageview(viewModel.image)
            label(viewModel.heading) {
                addClass(AppStyles.largeFont)
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
                button(getString("btn_dota_mods")) {
                    action { viewModel.onDotaModClicked() }
                }
            }
            label(viewModel.version) {
                addClass(AppStyles.smallFont, AppStyles.boldFont)
                paddingTop = PADDING_SMALL
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
        runLater {
            if (!ConfigPersistence.getAndSetNotifiedAboutNewMod()) {
                showInformation(getString("header_new_mod_system"), getString("content_new_mod_system"), icon = PoggiesIcon())
            }
        }
    }
}
