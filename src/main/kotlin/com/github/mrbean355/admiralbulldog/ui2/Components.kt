package com.github.mrbean355.admiralbulldog.ui2

import javafx.beans.property.DoubleProperty
import javafx.event.EventTarget
import javafx.scene.control.Slider
import tornadofx.slider

fun EventTarget.slider(min: Number, max: Number, valueProperty: DoubleProperty): Slider {
    return slider {
        valueProperty().bindBidirectional(valueProperty)
        this.min = min.toDouble()
        this.max = max.toDouble()
        majorTickUnit = this.max / 4
        minorTickCount = 4
        isShowTickMarks = true
        isShowTickLabels = true
        isSnapToTicks = true
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
