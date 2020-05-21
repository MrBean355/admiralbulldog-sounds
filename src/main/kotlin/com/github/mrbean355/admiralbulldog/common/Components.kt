package com.github.mrbean355.admiralbulldog.common

import javafx.beans.property.DoubleProperty
import javafx.event.EventTarget
import javafx.scene.control.Alert
import javafx.scene.control.ButtonBar
import javafx.scene.control.ButtonType
import javafx.scene.control.Slider
import tornadofx.FX
import tornadofx.slider

val RETRY_BUTTON = ButtonType(getString("btn_retry"), ButtonBar.ButtonData.OK_DONE)
val WHATS_NEW_BUTTON = ButtonType(getString("btn_whats_new"), ButtonBar.ButtonData.HELP_2)
val DOWNLOAD_BUTTON = ButtonType(getString("btn_download"), ButtonBar.ButtonData.NEXT_FORWARD)
val DISCORD_BUTTON = ButtonType("Discord", ButtonBar.ButtonData.OK_DONE)

inline fun confirmation(header: String, content: String? = null, vararg buttons: ButtonType, actionFn: Alert.(ButtonType) -> Unit = {}) =
        tornadofx.confirmation(header, content, *buttons, owner = FX.primaryStage, title = getString("title_app"), actionFn = actionFn)

inline fun information(header: String, content: String? = null, vararg buttons: ButtonType, actionFn: Alert.(ButtonType) -> Unit = {}) =
        tornadofx.information(header, content, *buttons, owner = FX.primaryStage, title = getString("title_app"), actionFn = actionFn)

inline fun warning(header: String, content: String? = null, vararg buttons: ButtonType, actionFn: Alert.(ButtonType) -> Unit = {}) =
        tornadofx.warning(header, content, *buttons, owner = FX.primaryStage, title = getString("title_app"), actionFn = actionFn)

inline fun error(header: String, content: String? = null, vararg buttons: ButtonType, actionFn: Alert.(ButtonType) -> Unit = {}) =
        tornadofx.error(header, content, *buttons, owner = FX.primaryStage, title = getString("title_app"), actionFn = actionFn)

fun EventTarget.slider(min: Number, max: Number, valueProperty: DoubleProperty, op: Slider.() -> Unit = {}): Slider {
    return slider {
        this.min = min.toDouble()
        this.max = max.toDouble()
        valueProperty().bindBidirectional(valueProperty)
        majorTickUnit = this.max / 4
        minorTickCount = 4
        isShowTickMarks = true
        isShowTickLabels = true
        isSnapToTicks = true
        op()
    }
}
