package com.github.mrbean355.admiralbulldog

import com.github.mrbean355.admiralbulldog.assets.SoundFile
import com.github.mrbean355.admiralbulldog.bytes.SoundByte
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.ListView
import javafx.scene.control.TextField
import javafx.scene.control.cell.CheckBoxListCell
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.VBox
import javafx.stage.Stage
import kotlin.reflect.KClass

class ChooseSoundFilesStage(private val type: KClass<out SoundByte>) : Stage() {
    private val allItems = SoundFile.values().map { it.name }
    private val toggles: Map<String, ObservableValue<Boolean>> = loadToggles()
    private val searchResults = FXCollections.observableArrayList(allItems)

    init {
        val root = VBox(8.0)
        root.padding = Insets(16.0)
        root.children += TextField().apply {
            textProperty().addListener { _, _, newValue ->
                searchResults.clear()
                searchResults.addAll(allItems.filter { it.contains(newValue.trim(), ignoreCase = true) })
            }
        }
        root.children += ListView<String>(searchResults).apply {
            cellFactory = CheckBoxListCell.forListView { toggles[it] }
        }
        root.children += Button("Save").apply {
            setOnAction { saveToggles() }
        }

        title = type.simpleName
        scene = Scene(root)
        addEventFilter(KeyEvent.KEY_PRESSED) {
            if (it.code == KeyCode.ESCAPE) {
                close()
            }
        }
    }

    private fun loadToggles(): Map<String, ObservableValue<Boolean>> {
        val selection = ConfigPersistence.getSoundsForType(type)
        return allItems.associateWith { SimpleBooleanProperty(it in selection) }
    }

    private fun saveToggles() {
        ConfigPersistence.saveSoundsForType(type, toggles.filterValues { it.value }.keys.toList())
        close()
    }
}