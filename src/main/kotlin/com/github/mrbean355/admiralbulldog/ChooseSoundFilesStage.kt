package com.github.mrbean355.admiralbulldog

import com.github.mrbean355.admiralbulldog.assets.SoundFile
import com.github.mrbean355.admiralbulldog.bytes.SoundByte
import com.github.mrbean355.admiralbulldog.game.playSound
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.collections.FXCollections
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.GridPane
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.stage.Stage
import kotlin.reflect.KClass

class ChooseSoundFilesStage(private val type: KClass<out SoundByte>) : Stage() {
    private val allItems = SoundFile.values().map { it.name }
    private val toggles: Map<String, BooleanProperty> = loadToggles()
    private val searchResults = FXCollections.observableArrayList(allItems)

    init {
        val root = VBox(PADDING_SMALL)
        root.padding = Insets(PADDING_MEDIUM)
        root.children += TextField().apply {
            textProperty().addListener { _, _, newValue ->
                searchResults.clear()
                searchResults.addAll(allItems.filter { it.contains(newValue.trim(), ignoreCase = true) })
            }
        }
        root.children += ListView<String>(searchResults).apply {
            setCellFactory { _ -> CheckBoxWithButtonCell { toggles[it] } }
        }
        root.children += Button(ACTION_SAVE).apply {
            setOnAction { saveToggles() }
        }

        title = type.simpleName
        scene = Scene(root)
        icons.add(bulldogIcon())
        width = WINDOW_WIDTH
        addEventFilter(KeyEvent.KEY_PRESSED) {
            if (it.code == KeyCode.ESCAPE) {
                close()
            }
        }
    }

    private fun loadToggles(): Map<String, BooleanProperty> {
        val selection = ConfigPersistence.getSoundsForType(type)
        return allItems.associateWith { SimpleBooleanProperty(it in selection) }
    }

    private fun saveToggles() {
        ConfigPersistence.saveSoundsForType(type, toggles.filterValues { it.value }.keys.toList())
        close()
    }

    private class CheckBoxWithButtonCell(private val getSelectedProperty: (String?) -> BooleanProperty?) : ListCell<String>() {
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

        override fun updateItem(item: String?, empty: Boolean) {
            super.updateItem(item, empty)
            if (empty) {
                graphic = null
                return
            }
            graphic = container
            checkBox.text = item
            button.setOnAction { playSound(item.orEmpty()) }

            booleanProperty?.let {
                checkBox.selectedProperty().unbindBidirectional(booleanProperty)
            }
            booleanProperty = getSelectedProperty(item)
            booleanProperty?.let {
                checkBox.selectedProperty().bindBidirectional(booleanProperty)
            }
        }
    }
}