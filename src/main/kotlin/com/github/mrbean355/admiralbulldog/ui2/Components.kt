package com.github.mrbean355.admiralbulldog.ui2

import com.github.mrbean355.admiralbulldog.playIcon
import com.github.mrbean355.admiralbulldog.ui.Alert
import com.github.mrbean355.admiralbulldog.ui.getString
import com.github.mrbean355.admiralbulldog.ui.toNullable
import javafx.beans.property.BooleanProperty
import javafx.beans.property.DoubleProperty
import javafx.event.EventTarget
import javafx.scene.control.Alert
import javafx.scene.control.Button
import javafx.scene.control.ButtonBar.ButtonData.OK_DONE
import javafx.scene.control.ButtonType
import javafx.scene.control.CheckBox
import javafx.scene.control.ListCell
import javafx.scene.control.Slider
import javafx.scene.image.ImageView
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.GridPane
import javafx.scene.layout.Priority
import javafx.stage.Stage
import javafx.stage.StageStyle
import tornadofx.UIComponent
import tornadofx.action
import tornadofx.slider

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

fun Slider.restrictMax(other: Slider) {
    valueProperty().addListener { _, _, newValue ->
        if (newValue.toDouble() > other.value) {
            other.value = newValue.toDouble()
        }
    }
}

fun Slider.restrictMin(other: Slider) {
    valueProperty().addListener { _, _, newValue ->
        if (newValue.toDouble() < other.value) {
            other.value = newValue.toDouble()
        }
    }
}

fun UIComponent.alert(
        type: Alert.AlertType,
        header: String,
        content: String,
        buttons: Array<ButtonType> = emptyArray()
): Alert {
    return Alert(type, header, content, buttons, owner = currentWindow)
}

object CustomButtonType {
    val RETRY = ButtonType(getString("action_retry"), OK_DONE)
    val DOWNLOAD = ButtonType(getString("action_download"), OK_DONE)
}

/** Open a modal which shows a confirmation alert when closing. */
fun UIComponent.openUpdateModal(): Stage? {
    return openModal(stageStyle = StageStyle.UTILITY, escapeClosesWindow = false, resizable = false)?.also { stage ->
        stage.setOnCloseRequest {
            it.consume()
            val action = alert(Alert.AlertType.WARNING, getString("header_cancel_update"), getString("content_cancel_update"), arrayOf(ButtonType.YES, ButtonType.NO))
                    .showAndWait()
                    .toNullable()
            if (action == ButtonType.YES) {
                stage.close()
            }
        }
    }
}

class CheckBoxWithButtonCell(
        private val onButtonClicked: (SoundBite) -> Unit,
        private val getSelectedProperty: (SoundBite) -> BooleanProperty
) : ListCell<SoundBite>() {
    private val container = GridPane()
    private val checkBox = CheckBox()
    private val button = Button("", ImageView(playIcon()))
    private var booleanProperty: BooleanProperty? = null

    init {
        container.columnConstraints.addAll(ColumnConstraints().apply {
            hgrow = Priority.ALWAYS
        })
        container.add(checkBox, 0, 0)
        container.add(button, 1, 0)
    }

    override fun updateItem(item: SoundBite?, empty: Boolean) {
        super.updateItem(item, empty)
        if (empty || item == null) {
            graphic = null
            return
        }
        graphic = container
        checkBox.text = item.name
        button.action { onButtonClicked(item) }

        booleanProperty?.let {
            checkBox.selectedProperty().unbindBidirectional(booleanProperty)
        }
        booleanProperty = getSelectedProperty(item)
        booleanProperty?.let {
            checkBox.selectedProperty().bindBidirectional(booleanProperty)
        }
    }
}
