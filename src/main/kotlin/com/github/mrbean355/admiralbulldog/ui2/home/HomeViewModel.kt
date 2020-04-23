package com.github.mrbean355.admiralbulldog.ui2.home

import com.github.mrbean355.admiralbulldog.ui.getString
import com.github.mrbean355.admiralbulldog.ui2.PoggiesIcon
import com.github.mrbean355.admiralbulldog.ui2.WeirdChampIcon
import javafx.beans.property.SimpleBooleanProperty
import tornadofx.booleanBinding
import tornadofx.objectBinding
import tornadofx.stringBinding

class HomeViewModel {
    private val isConnected = SimpleBooleanProperty(false)

    val image = isConnected.objectBinding {
        if (it == true) PoggiesIcon() else WeirdChampIcon()
    }
    val label = isConnected.stringBinding {
        getString(if (it == true) "dota_connected_title" else "waiting_for_dota_title")
    }
    val progressBarVisible = isConnected.booleanBinding {
        it != true
    }
    val description = isConnected.stringBinding {
        getString(if (it == true) "dota_connected_description" else "waiting_for_dota_description")
    }
}
