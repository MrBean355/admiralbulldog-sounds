package com.github.mrbean355.admiralbulldog.ui2.sounds

import com.github.mrbean355.admiralbulldog.playIcon
import com.github.mrbean355.admiralbulldog.ui2.SoundBite
import javafx.scene.control.TreeCell
import javafx.scene.image.ImageView
import tornadofx.action
import tornadofx.button
import tornadofx.hbox
import tornadofx.label
import tornadofx.spacer

class SoundBiteTreeCell : TreeCell<SoundBiteTreeCell.Model>() {

    override fun updateItem(item: Model?, empty: Boolean) {
        super.updateItem(item, empty)
        if (item == null || empty) {
            graphic = null
        } else {
            graphic = hbox {
                label(item.label)
                spacer()
                if (item.soundBite != null) {
                    button(graphic = ImageView(playIcon())) {
                        action {
                            item.onButtonClicked(item.soundBite)
                        }
                    }
                }
            }
        }
    }

    class Model(
            val label: String,
            val soundBite: SoundBite? = null,
            val onButtonClicked: (SoundBite) -> Unit = {}
    )
}