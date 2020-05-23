package com.github.mrbean355.admiralbulldog.discord

import com.github.mrbean355.admiralbulldog.common.PADDING_MEDIUM
import com.github.mrbean355.admiralbulldog.common.PADDING_SMALL
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.common.useBoldFont
import com.github.mrbean355.admiralbulldog.common.useMonospacedFont
import tornadofx.Fragment
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
                useBoldFont()
                gridpaneConstraints {
                    columnSpan = 2
                }
            }
        }
        row {
            label("!help") {
                useMonospacedFont()
            }
            label(getString("command_description_help"))
        }
        row {
            label("!roons") {
                useMonospacedFont()
            }
            label(getString("command_description_roons"))
        }
        row {
            label("!seeya") {
                useMonospacedFont()
            }
            label(getString("command_description_seeya"))
        }
        row {
            label("!magic") {
                useMonospacedFont()
            }
            label(getString("command_description_magic"))
        }
        row {
            label("!follow") {
                useMonospacedFont()
            }
            label(getString("command_description_follow"))
        }
        row {
            label("!unfollow") {
                useMonospacedFont()
            }
            label(getString("command_description_unfollow"))
        }
        row {
            label("!volume") {
                useMonospacedFont()
            }
            label(getString("command_description_get_volume"))
        }
        row {
            label("!volume x") {
                useMonospacedFont()
            }
            label(getString("command_description_set_volume"))
        }
    }
}