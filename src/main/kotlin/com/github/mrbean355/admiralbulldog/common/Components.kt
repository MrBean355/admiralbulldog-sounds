package com.github.mrbean355.admiralbulldog.common

import javafx.beans.property.BooleanProperty
import javafx.beans.property.DoubleProperty
import javafx.event.EventTarget
import javafx.geometry.Pos.CENTER_LEFT
import javafx.scene.control.Alert
import javafx.scene.control.Button
import javafx.scene.control.ButtonBar
import javafx.scene.control.ButtonType
import javafx.scene.control.CheckBox
import javafx.scene.control.Label
import javafx.scene.control.ListCell
import javafx.scene.control.ListView
import javafx.scene.control.Slider
import javafx.scene.control.Tooltip
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.Priority
import javafx.util.StringConverter
import tornadofx.FX
import tornadofx.booleanProperty
import tornadofx.paddingLeft
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

@Suppress("FunctionName")
fun RateStringConverter(): StringConverter<Double> {
    return object : StringConverter<Double>() {

        override fun toString(input: Double?): String {
            input ?: return ""
            return (input / 100.0).toString() + "x"
        }

        override fun fromString(input: String?): Double {
            input ?: return 0.0
            return input.toDouble()
        }
    }
}

@Suppress("FunctionName")
fun PeriodStringConverter(): StringConverter<Number> {
    return object : StringConverter<Number>() {

        override fun toString(input: Number?): String {
            return input?.toString() ?: ""
        }

        override fun fromString(input: String?): Number {
            return input?.toIntOrNull() ?: 0
        }
    }
}

fun <T> ListView<T>.useLabelWithButton(stringConverter: (T) -> String, onButtonClicked: (T) -> Unit) {
    setCellFactory {
        CheckBoxWithButtonCell(false, stringConverter, { booleanProperty() }, onButtonClicked)
    }
}

fun <T> ListView<T>.useCheckBoxWithButton(stringConverter: (T) -> String, getSelectedProperty: (T) -> BooleanProperty, onButtonClicked: (T) -> Unit) {
    setCellFactory {
        CheckBoxWithButtonCell(true, stringConverter, getSelectedProperty, onButtonClicked)
    }
}

private class CheckBoxWithButtonCell<T>(
        private val showCheckBox: Boolean,
        private val stringConverter: (T) -> String,
        private val getSelectedProperty: (T) -> BooleanProperty,
        private val onButtonClicked: (T) -> Unit
) : ListCell<T>() {
    private val container = HBox()
    private val checkBox = CheckBox()
    private val label = Label()
    private val button = Button("", ImageView(PlayIcon()))
    private var booleanProperty: BooleanProperty? = null

    init {
        container.apply {
            alignment = CENTER_LEFT
            if (showCheckBox) {
                children += checkBox
            }
            children += label.apply {
                if (showCheckBox) {
                    paddingLeft = PADDING_SMALL
                }
            }
            children += Pane().apply {
                HBox.setHgrow(this, Priority.ALWAYS)
            }
            children += button
        }
    }

    override fun updateItem(item: T?, empty: Boolean) {
        super.updateItem(item, empty)
        if (empty || item == null) {
            graphic = null
            return
        }
        graphic = container
        label.text = stringConverter(item)
        button.setOnAction { onButtonClicked(item) }
        button.tooltip = Tooltip(getString("tooltip_play_locally"))

        booleanProperty?.let {
            checkBox.selectedProperty().unbindBidirectional(booleanProperty)
        }
        booleanProperty = getSelectedProperty(item)
        booleanProperty?.let {
            checkBox.selectedProperty().bindBidirectional(booleanProperty)
        }
    }
}
