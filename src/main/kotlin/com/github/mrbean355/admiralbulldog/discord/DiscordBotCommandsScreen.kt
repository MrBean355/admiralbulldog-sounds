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

package com.github.mrbean355.admiralbulldog.discord

import com.github.mrbean355.admiralbulldog.common.PADDING_MEDIUM
import com.github.mrbean355.admiralbulldog.common.PADDING_SMALL
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.styles.AppStyles
import tornadofx.Fragment
import tornadofx.addClass
import tornadofx.gridpane
import tornadofx.gridpaneConstraints
import tornadofx.label
import tornadofx.paddingAll
import tornadofx.row

class DiscordBotCommandsScreen : Fragment(getString("title_discord_bot_commands")) {

    override val root = gridpane {
        paddingAll = PADDING_MEDIUM
        hgap = PADDING_MEDIUM
        vgap = PADDING_SMALL
        row {
            label(getString("discord_command_header")) {
                addClass(AppStyles.boldFont)
                gridpaneConstraints {
                    columnSpan = 2
                }
            }
        }
        row {
            label("!help") {
                addClass(AppStyles.monospacedFont)
            }
            label(getString("command_description_help"))
        }
        row {
            label("!roons") {
                addClass(AppStyles.monospacedFont)
            }
            label(getString("command_description_roons"))
        }
        row {
            label("!seeya") {
                addClass(AppStyles.monospacedFont)
            }
            label(getString("command_description_seeya"))
        }
        row {
            label("!magic") {
                addClass(AppStyles.monospacedFont)
            }
            label(getString("command_description_magic"))
        }
        row {
            label("!follow") {
                addClass(AppStyles.monospacedFont)
            }
            label(getString("command_description_follow"))
        }
        row {
            label("!unfollow") {
                addClass(AppStyles.monospacedFont)
            }
            label(getString("command_description_unfollow"))
        }
        row {
            label("!volume") {
                addClass(AppStyles.monospacedFont)
            }
            label(getString("command_description_get_volume"))
        }
        row {
            label("!volume x") {
                addClass(AppStyles.monospacedFont)
            }
            label(getString("command_description_set_volume"))
        }
    }
}