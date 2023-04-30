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

package com.github.mrbean355.admiralbulldog.settings

import com.github.mrbean355.admiralbulldog.common.PADDING_MEDIUM
import com.github.mrbean355.admiralbulldog.common.PADDING_SMALL
import com.github.mrbean355.admiralbulldog.common.URL_BULLDOG_TWITCH
import com.github.mrbean355.admiralbulldog.common.URL_DISCORD_SERVER_INVITE
import com.github.mrbean355.admiralbulldog.common.URL_PAYPAL
import com.github.mrbean355.admiralbulldog.common.URL_PROJECT_WEBSITE
import com.github.mrbean355.admiralbulldog.common.URL_TELEGRAM_CHANNEL
import com.github.mrbean355.admiralbulldog.common.URL_TWITCH_CHANNEL
import com.github.mrbean355.admiralbulldog.common.WINDOW_WIDTH_LARGE
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.styles.AppStyles
import tornadofx.View
import tornadofx.action
import tornadofx.addClass
import tornadofx.hyperlink
import tornadofx.label
import tornadofx.paddingAll
import tornadofx.vbox

class MoreInformationScreen : View(getString("title_more_information")) {

    override val root = vbox(spacing = PADDING_SMALL) {
        paddingAll = PADDING_MEDIUM
        prefWidth = WINDOW_WIDTH_LARGE
        label(getString("label_more_information_project_dev"))
        hyperlink(getString("btn_project_website")) {
            action { hostServices.showDocument(URL_PROJECT_WEBSITE) }
        }
        hyperlink(getString("btn_telegram_channel")) {
            action { hostServices.showDocument(URL_TELEGRAM_CHANNEL) }
        }
        hyperlink(getString("btn_twitch_channel")) {
            action { hostServices.showDocument(URL_TWITCH_CHANNEL) }
        }
        label(getString("label_need_help"))
        hyperlink(getString("btn_discord_community")) {
            action { hostServices.showDocument(URL_DISCORD_SERVER_INVITE) }
        }
        label(getString("label_more_information_donate")) {
            isWrapText = true
        }
        hyperlink(getString("btn_paypal")) {
            action { hostServices.showDocument(URL_PAYPAL) }
        }
        label(getString("label_not_affiliated")) {
            addClass(AppStyles.italicFont)
            isWrapText = true
        }
        hyperlink(getString("btn_bulldog_twitch")) {
            action { hostServices.showDocument(URL_BULLDOG_TWITCH) }
        }
    }
}