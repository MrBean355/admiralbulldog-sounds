package com.github.mrbean355.admiralbulldog.ui2.sounds

import com.github.mrbean355.admiralbulldog.ui.getString
import com.github.mrbean355.admiralbulldog.ui2.Spacing
import com.github.mrbean355.admiralbulldog.ui2.triggers.SoundTrigger
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

class SoundTriggersScreen : View(getString("tab_sounds")) {
    override val root = scrollpane(fitToWidth = true) {
        vbox {
            SoundTrigger.onEach { trigger ->
                label(trigger.name) {
                    paddingHorizontal = Spacing.MEDIUM * 2
                    paddingVertical = Spacing.SMALL * 2
                    fitToParentWidth()
                    onHover { isHovering ->
                        background = if (isHovering) HOVER_BACKGROUND else INACTIVE_BACKGROUND
                    }
                    setOnMouseClicked {
                        find<ConfigureSoundTriggerScreen>(params = ConfigureSoundTriggerScreen.params(trigger))
                                .openModal(resizable = false)
                    }
                }
                separator()
            }
        }
    }
}
