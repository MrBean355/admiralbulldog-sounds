package com.github.mrbean355.admiralbulldog.settings

import com.github.mrbean355.admiralbulldog.arch.ComposeViewModel
import com.github.mrbean355.admiralbulldog.common.browseUrl

class MoreInformationViewModel : ComposeViewModel() {

    fun openUrl(url: String) {
        browseUrl(url)
    }
}
