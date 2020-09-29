package com.github.mrbean355.admiralbulldog.common

import com.github.mrbean355.admiralbulldog.AppStyles
import com.github.mrbean355.admiralbulldog.assets.SoundBite
import com.github.mrbean355.admiralbulldog.assets.SoundBites
import javafx.beans.property.BooleanProperty
import javafx.beans.property.IntegerProperty
import javafx.event.EventTarget
import javafx.geometry.Pos.CENTER_LEFT
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.Priority.ALWAYS
import javafx.util.StringConverter
import tornadofx.*

val RETRY_BUTTON = ButtonType(getString("btn_retry"), ButtonBar.ButtonData.OK_DONE)
val WHATS_NEW_BUTTON = ButtonType(getString("btn_whats_new"), ButtonBar.ButtonData.HELP_2)
val UPDATE_BUTTON = ButtonType(getString("btn_update"), ButtonBar.ButtonData.NEXT_FORWARD)
val DISCORD_BUTTON = ButtonType(getString("btn_join_discord"), ButtonBar.ButtonData.OK_DONE)

inline fun confirmation(header: String, content: String? = null, vararg buttons: ButtonType, actionFn: Alert.(ButtonType) -> Unit = {}) =
        confirmation(header, content, *buttons, owner = FX.primaryStage, title = getString("title_app"), actionFn = actionFn)

inline fun information(header: String, content: String? = null, vararg buttons: ButtonType, actionFn: Alert.(ButtonType) -> Unit = {}) =
        information(header, content, *buttons, owner = FX.primaryStage, title = getString("title_app"), actionFn = actionFn)

inline fun warning(header: String, content: String? = null, vararg buttons: ButtonType, actionFn: Alert.(ButtonType) -> Unit = {}) =
        warning(header, content, *buttons, owner = FX.primaryStage, title = getString("title_app"), actionFn = actionFn)

inline fun error(header: String, content: String? = null, vararg buttons: ButtonType, actionFn: Alert.(ButtonType) -> Unit = {}) =
        error(header, content, *buttons, owner = FX.primaryStage, title = getString("title_app"), actionFn = actionFn)

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

fun <T> ListView<T>.useLabelWithButton(buttonImage: Image, buttonTooltip: String, stringConverter: (T) -> String, onButtonClicked: (T) -> Unit) {
    setCellFactory {
        CheckBoxWithButtonCell(false, buttonImage, buttonTooltip, stringConverter, { booleanProperty() }, onButtonClicked)
    }
}

fun <T> ListView<T>.useCheckBoxWithButton(buttonImage: Image, buttonTooltip: String, stringConverter: (T) -> String, getSelectedProperty: (T) -> BooleanProperty, onButtonClicked: (T) -> Unit) {
    setCellFactory {
        CheckBoxWithButtonCell(true, buttonImage, buttonTooltip, stringConverter, getSelectedProperty, onButtonClicked)
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
        private val onButtonClicked: (T) -> Unit
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
        button.setOnAction { onButtonClicked(item) }
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
