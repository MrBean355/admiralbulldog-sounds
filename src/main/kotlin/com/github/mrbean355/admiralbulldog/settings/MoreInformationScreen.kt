package com.github.mrbean355.admiralbulldog.settings

import com.github.mrbean355.admiralbulldog.AppStyles
import com.github.mrbean355.admiralbulldog.common.*
import tornadofx.*

class MoreInformationScreen : View(getString("title_more_information")) {

    override val root = vbox(spacing = PADDING_SMALL) {
        paddingAll = PADDING_MEDIUM
        prefWidth = WINDOW_WIDTH_LARGE
        label(getString("label_more_information_project_dev"))
        hyperlink(getString("btn_project_website")) {
            action { hostServices.showDocument(URL_PROJECT_WEBSITE) }
        }
        hyperlink(getString("btn_discord_community")) {
            action { hostServices.showDocument(URL_DISCORD_SERVER_INVITE) }
        }
        hyperlink(getString("btn_telegram_channel")) {
            action { hostServices.showDocument(URL_TELEGRAM_CHANNEL) }
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