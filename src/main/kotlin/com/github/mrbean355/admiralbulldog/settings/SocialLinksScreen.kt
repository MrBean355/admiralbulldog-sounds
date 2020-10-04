package com.github.mrbean355.admiralbulldog.settings

import com.github.mrbean355.admiralbulldog.common.*
import tornadofx.*

class SocialLinksScreen : View(getString("title_social_links")) {

    override val root = vbox(spacing = PADDING_SMALL) {
        paddingAll = PADDING_MEDIUM
        label(getString("label_social_links_info"))
        hyperlink(getString("btn_project_website")) {
            action { hostServices.showDocument(URL_PROJECT_WEBSITE) }
        }
        hyperlink(getString("btn_discord_community")) {
            action { hostServices.showDocument(URL_DISCORD_SERVER_INVITE) }
        }
        hyperlink(getString("btn_telegram_channel")) {
            action { hostServices.showDocument(URL_TELEGRAM_CHANNEL) }
        }
        hyperlink(getString("btn_bulldog_twitch")) {
            action { hostServices.showDocument(URL_BULLDOG_TWITCH) }
        }
        label(getString("label_social_links_donate"))
        hyperlink(getString("btn_paypal")) {
            action { hostServices.showDocument(URL_PAYPAL) }
        }
    }
}