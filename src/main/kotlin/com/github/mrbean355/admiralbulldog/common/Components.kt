/*
 * Copyright 2022 Michael Johnston
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.mrbean355.admiralbulldog.common

import com.github.mrbean355.admiralbulldog.assets.SoundBite
import com.github.mrbean355.admiralbulldog.assets.SoundBites
import com.github.mrbean355.admiralbulldog.styles.AppStyles
import javafx.beans.property.BooleanProperty
import javafx.beans.property.IntegerProperty
import javafx.beans.property.Property
import javafx.event.EventTarget
import javafx.geometry.Pos.CENTER_LEFT
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.control.Label
import javafx.scene.control.ListCell
import javafx.scene.control.ListView
import javafx.scene.control.Slider
import javafx.scene.control.Spinner
import javafx.scene.control.TableCell
import javafx.scene.control.TableColumn
import javafx.scene.control.Tooltip
import javafx.scene.control.TreeCell
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.Priority.ALWAYS
import javafx.util.StringConverter
import tornadofx.action
import tornadofx.addClass
import tornadofx.booleanProperty
import tornadofx.button
import tornadofx.hbox
import tornadofx.hgrow
import tornadofx.label
import tornadofx.managedWhen
import tornadofx.paddingLeft
import tornadofx.rowItem
import tornadofx.slider
import tornadofx.spinner

fun EventTarget.chanceSpinner(property: IntegerProperty, block: Spinner<Number>.() -> Unit): Spinner<Number> {
    return spinner(min = MIN_CHANCE, max = MAX_CHANCE, amountToStepBy = CHANCE_STEP, property = property, editable = true, enableScroll = true) {
        valueFactory.converter = IntStringConverter()
        block()
    }
}

fun EventTarget.periodSpinner(property: IntegerProperty, block: Spinner<Number>.() -> Unit = {}): Spinner<Number> {
    return spinner(min = MIN_PERIOD, max = MAX_PERIOD, amountToStepBy = PERIOD_STEP, property = property, editable = true, enableScroll = true) {
        valueFactory.converter = IntStringConverter()
        block()
    }
}

fun EventTarget.rateSpinner(property: IntegerProperty, block: Spinner<Number>.() -> Unit = {}): Spinner<Number> {
    return spinner(min = MIN_RATE, max = MAX_RATE, amountToStepBy = RATE_STEP, property = property, editable = true, enableScroll = true) {
        valueFactory.converter = IntStringConverter()
        block()
    }
}

fun EventTarget.volumeSpinner(property: IntegerProperty, max: Int = MAX_VOLUME, block: Spinner<Number>.() -> Unit = {}): Spinner<Number> {
    return spinner(min = MIN_VOLUME, max = max, amountToStepBy = VOLUME_STEP, property = property, editable = true, enableScroll = true) {
        valueFactory.converter = IntStringConverter()
        block()
    }
}

fun EventTarget.bountyRuneSpinner(property: IntegerProperty, block: Spinner<Number>.() -> Unit = {}): Spinner<Number> {
    return spinner(min = MIN_BOUNTY_RUNE_TIMER, max = MAX_BOUNTY_RUNE_TIMER, amountToStepBy = BOUNTY_RUNE_TIMER_STEP, property = property, editable = true, enableScroll = true) {
        valueFactory.converter = IntStringConverter()
        block()
    }
}

fun EventTarget.ratingSlider(property: Property<Number>, block: Slider.() -> Unit = {}): Slider {
    return slider(min = 1, max = 5) {
        valueProperty().bindBidirectional(property)
        majorTickUnit = 1.0
        isSnapToTicks = true
        isShowTickLabels = true
        isShowTickMarks = true
        minorTickCount = 0
        block()
    }
}

@Suppress("FunctionName")
private fun IntStringConverter(): StringConverter<Number> {
    return object : StringConverter<Number>() {

        override fun toString(input: Number?): String {
            return input?.toString().orEmpty()
        }

        override fun fromString(input: String?): Number {
            return input.orEmpty().toIntOrNull() ?: 0
        }
    }
}

fun <T> ListView<T>.useLabelWithButton(buttonImage: Image, buttonTooltip: String, stringConverter: (T) -> String, onButtonClicked: (T, Int) -> Unit) {
    setCellFactory {
        CheckBoxWithButtonCell(false, buttonImage, buttonTooltip, stringConverter, { booleanProperty() }, onButtonClicked)
    }
}

fun <T> ListView<T>.useCheckBoxWithButton(buttonImage: Image, buttonTooltip: String, stringConverter: (T) -> String, getSelectedProperty: (T) -> BooleanProperty, onButtonClicked: (T) -> Unit) {
    setCellFactory {
        CheckBoxWithButtonCell(true, buttonImage, buttonTooltip, stringConverter, getSelectedProperty, { item, _ -> onButtonClicked(item) })
    }
}

fun TableColumn<SoundBite, String>.useLabelWithPlayButton(onButtonClicked: (SoundBite) -> Unit) {
    setCellFactory {
        SoundBiteTableCell(onButtonClicked)
    }
}

private class CheckBoxWithButtonCell<T>(
    private val showCheckBox: Boolean,
    buttonImage: Image,
    private val buttonTooltip: String,
    private val stringConverter: (T) -> String,
    private val getSelectedProperty: (T) -> BooleanProperty,
    private val onButtonClicked: (T, Int) -> Unit
) : ListCell<T>() {
    private val container = HBox()
    private val checkBox = CheckBox()
    private val label = Label()
    private val button = Button("", ImageView(buttonImage))
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
                hgrow = ALWAYS
            }
            children += button.apply {
                addClass(AppStyles.iconButton)
            }
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
        button.setOnAction { onButtonClicked(item, index) }
        button.tooltip = Tooltip(buttonTooltip)

        booleanProperty?.let {
            checkBox.selectedProperty().unbindBidirectional(booleanProperty)
        }
        booleanProperty = getSelectedProperty(item)
        booleanProperty?.let {
            checkBox.selectedProperty().bindBidirectional(booleanProperty)
        }
    }
}

private class SoundBiteTableCell(private val onButtonClicked: (SoundBite) -> Unit) : TableCell<SoundBite, String>() {

    override fun updateItem(item: String?, empty: Boolean) {
        super.updateItem(item, empty)
        if (item == null || empty) {
            graphic = null
            return
        }
        graphic = hbox(spacing = PADDING_SMALL, alignment = CENTER_LEFT) {
            button(graphic = ImageView(PlayIcon())) {
                addClass(AppStyles.iconButton)
                action { onButtonClicked(rowItem) }
            }
            label(item)
        }
    }
}

// ========================= SOUND BITE TREE CELL ========================= \\

fun TreeView<SoundBiteTreeModel>.useSoundBiteCells(onPlayClicked: (SoundBite) -> Unit) {
    setCellFactory {
        SoundBiteTreeCell(onPlayClicked)
    }
}

@Suppress("FunctionName")
fun SoundBiteTreeItem(label: String, items: Collection<String>): TreeItem<SoundBiteTreeModel> {
    return TreeItem(SoundBiteTreeModel("$label (${items.size})")).apply {
        children += items.sorted().map {
            val soundName = it.substringBeforeLast('.')
            TreeItem(SoundBiteTreeModel(soundName, SoundBites.findSound(soundName)))
        }
    }
}

class SoundBiteTreeModel(label: String, val soundBite: SoundBite? = null) {
    val label: String = soundBite?.name ?: label
}

private class SoundBiteTreeCell(private val onPlayClicked: (SoundBite) -> Unit) : TreeCell<SoundBiteTreeModel>() {
    private val root = HBox()
    private val label = Label()
    private val button = Button("", ImageView(PlayIcon()))

    init {
        root.apply {
            alignment = CENTER_LEFT
            children += label
            children += Pane().apply {
                hgrow = ALWAYS
            }
            children += button.apply {
                addClass(AppStyles.iconButton)
                managedWhen(visibleProperty())
            }
            children += Pane().apply {
                prefWidth = PADDING_TINY
            }
        }
    }

    override fun updateItem(item: SoundBiteTreeModel?, empty: Boolean) {
        super.updateItem(item, empty)
        if (item == null || empty) {
            graphic = null
            return
        }
        label.text = item.label
        button.isVisible = item.soundBite != null
        button.action {
            item.soundBite?.let(onPlayClicked)
        }
        graphic = root
    }
}

// ========================= VOLUME LIST CELL ========================= \\

fun ListView<Volume>.useVolumeCells() {
    setCellFactory {
        VolumeListCell()
    }
}

class Volume(val name: String, val volume: Int)

private class VolumeListCell : ListCell<Volume>() {
    private val root = HBox()
    private val label = Label()
    private val volume = Label()

    init {
        root.apply {
            children += label
            children += Pane().apply {
                hgrow = ALWAYS
            }
            children += volume
        }
    }

    override fun updateItem(item: Volume?, empty: Boolean) {
        super.updateItem(item, empty)
        if (item == null || empty) {
            graphic = null
            return
        }
        label.text = item.name
        volume.text = "${item.volume}%"
        graphic = root
    }
}
