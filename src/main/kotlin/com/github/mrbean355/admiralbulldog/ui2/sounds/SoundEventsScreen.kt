package com.github.mrbean355.admiralbulldog.ui2.sounds

import com.github.mrbean355.admiralbulldog.ui.getString
import com.github.mrbean355.admiralbulldog.ui2.Spacing
import com.github.mrbean355.admiralbulldog.ui2.events.ALL_EVENTS
import javafx.geometry.Insets
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.scene.paint.Color
import tornadofx.View
import tornadofx.fitToParentWidth
import tornadofx.label
import tornadofx.onHover
import tornadofx.paddingHorizontal
import tornadofx.paddingVertical
import tornadofx.scrollpane
import tornadofx.separator
import tornadofx.vbox

private val HOVER_BACKGROUND = Background(BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY, Insets.EMPTY))
private val INACTIVE_BACKGROUND = Background(BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY))

class SoundEventsScreen : View(getString("tab_sounds")) {
    override val root = scrollpane(fitToWidth = true) {
        vbox {
            ALL_EVENTS.forEach { soundEvent ->
                label(soundEvent.name) {
                    paddingHorizontal = Spacing.MEDIUM * 2
                    paddingVertical = Spacing.SMALL * 2
                    fitToParentWidth()
                    onHover { isHovering ->
                        background = if (isHovering) HOVER_BACKGROUND else INACTIVE_BACKGROUND
                    }
                    setOnMouseClicked {
                        find<ConfigureSoundEventScreen>(params = ConfigureSoundEventScreen.params(soundEvent))
                                .openModal(resizable = false)
                    }
                }
                separator()
            }
        }
    }
}
