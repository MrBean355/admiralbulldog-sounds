/*
 * Copyright 2023 Michael Johnston
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.mrbean355.admiralbulldog.sounds

import com.github.mrbean355.admiralbulldog.common.HelpIcon
import com.github.mrbean355.admiralbulldog.common.PADDING_MEDIUM
import com.github.mrbean355.admiralbulldog.common.PADDING_SMALL
import com.github.mrbean355.admiralbulldog.common.WINDOW_WIDTH_LARGE
import com.github.mrbean355.admiralbulldog.common.bountyRuneSpinner
import com.github.mrbean355.admiralbulldog.common.chanceSpinner
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.common.periodSpinner
import com.github.mrbean355.admiralbulldog.common.rateSpinner
import com.github.mrbean355.admiralbulldog.common.showInformation
import com.github.mrbean355.admiralbulldog.styles.AppStyles
import com.github.mrbean355.admiralbulldog.triggers.SoundTriggerType
import javafx.scene.image.ImageView
import tornadofx.Fragment
import tornadofx.Scope
import tornadofx.action
import tornadofx.addClass
import tornadofx.button
import tornadofx.checkbox
import tornadofx.enableWhen
import tornadofx.field
import tornadofx.fieldset
import tornadofx.form
import tornadofx.label
import tornadofx.managedWhen
import tornadofx.paddingAll
import tornadofx.paddingBottom
import tornadofx.visibleWhen
import tornadofx.whenUndocked

class ConfigureSoundTriggerScreen : Fragment() {
    private val viewModel by inject<ConfigureSoundTriggerViewModel>(Scope(), params)

    override val root = form {
        prefWidth = WINDOW_WIDTH_LARGE
        paddingAll = PADDING_MEDIUM
        fieldset {
            label(viewModel.description) {
                addClass(AppStyles.mediumFont, AppStyles.boldFont)
                paddingBottom = PADDING_SMALL
                isWrapText = true
            }
            field(getString("label_enable_sound_trigger")) {
                checkbox(property = viewModel.enabled)
            }
            field(getString("label_rune_timer")) {
                visibleWhen(viewModel.showBountyRuneTimer)
                managedWhen(visibleProperty())
                bountyRuneSpinner(viewModel.bountyRuneTimer)
                button(graphic = ImageView(HelpIcon())) {
                    action { showInformation(getString("header_about_rune_timer"), getString("content_about_rune_timer")) }
                }
            }
            field(getString("label_rune_timer")) {
                visibleWhen(viewModel.showWisdomRuneTimer)
                managedWhen(visibleProperty())
                bountyRuneSpinner(viewModel.wisdomRuneTimer)
                button(graphic = ImageView(HelpIcon())) {
                    action { showInformation(getString("header_about_rune_timer"), getString("content_about_rune_timer")) }
                }
            }
        }
        fieldset(getString("header_chance_to_play")) {
            visibleWhen(viewModel.showChance)
            managedWhen(visibleProperty())
            field(getString("label_use_smart_chance")) {
                visibleWhen(viewModel.showSmartChance)
                managedWhen(visibleProperty())
                checkbox(property = viewModel.useSmartChance)
                button(graphic = ImageView(HelpIcon())) {
                    action { showInformation(getString("header_about_smart_chance"), getString("content_about_smart_chance")) }
                }
            }
            field(getString("label_chance_to_play")) {
                chanceSpinner(viewModel.chance) {
                    enableWhen(viewModel.enableChanceSpinner)
                }
            }
        }
        fieldset(getString("header_periodic_trigger")) {
            visibleWhen(viewModel.showPeriod)
            managedWhen(visibleProperty())
            field(getString("label_periodic_trigger_min")) {
                periodSpinner(viewModel.minPeriod)
                label(getString("label_periodic_trigger_minutes"))
            }
            field(getString("label_periodic_trigger_max")) {
                periodSpinner(viewModel.maxPeriod)
                label(getString("label_periodic_trigger_minutes"))
                button(graphic = ImageView(HelpIcon())) {
                    action { showInformation(getString("header_about_periodic_trigger"), getString("content_about_periodic_trigger")) }
                }
            }
        }
        fieldset(getString("header_playback_speed")) {
            field(getString("label_min_playback_speed")) {
                rateSpinner(viewModel.minRate)
            }
            field(getString("label_max_playback_speed")) {
                rateSpinner(viewModel.maxRate)
                button(graphic = ImageView(HelpIcon())) {
                    action { showInformation(getString("header_about_playback_speed"), getString("content_about_playback_speed")) }
                }
            }
            button(getString("btn_test_playback_speed")) {
                action { find<TestPlaybackSpeedScreen>().openModal(resizable = false) }
            }
        }
        fieldset(getString("header_sound_bite_selection")) {
            field(getString("label_sound_bite_count")) {
                label(viewModel.soundBiteCount)
            }
            button(getString("btn_choose_sounds")) {
                action { viewModel.onChooseSoundsClicked() }
            }
        }
    }

    init {
        titleProperty.bind(viewModel.title)
        whenUndocked {
            viewModel.onUndock()
        }
    }

    companion object {

        fun params(type: SoundTriggerType): Map<String, Any?> {
            return mapOf("type" to type)
        }
    }
}