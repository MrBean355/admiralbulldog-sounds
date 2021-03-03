/*
 * Copyright 2021 Michael Johnston
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.mrbean355.admiralbulldog.home

import com.github.mrbean355.admiralbulldog.common.PADDING_MEDIUM
import com.github.mrbean355.admiralbulldog.common.PADDING_SMALL
import com.github.mrbean355.admiralbulldog.common.SettingsIcon
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.settings.SettingsScreen
import com.github.mrbean355.admiralbulldog.styles.AppStyles
import javafx.geometry.Pos
import javafx.geometry.Pos.CENTER
import javafx.scene.image.ImageView
import tornadofx.View
import tornadofx.action
import tornadofx.addClass
import tornadofx.button
import tornadofx.fitToParentWidth
import tornadofx.hbox
import tornadofx.imageview
import tornadofx.insets
import tornadofx.label
import tornadofx.managedWhen
import tornadofx.paddingAll
import tornadofx.paddingTop
import tornadofx.progressbar
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
    }
}
