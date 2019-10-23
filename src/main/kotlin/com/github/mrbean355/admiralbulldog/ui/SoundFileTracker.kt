package com.github.mrbean355.admiralbulldog.ui

import com.github.mrbean355.admiralbulldog.PROMPT_SEARCH
import com.github.mrbean355.admiralbulldog.TOOLTIP_PLAY_LOCALLY
import com.github.mrbean355.admiralbulldog.assets.SoundFile
import com.github.mrbean355.admiralbulldog.assets.SoundFiles
import com.github.mrbean355.admiralbulldog.playIcon
import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.collections.FXCollections
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.control.ListCell
import javafx.scene.control.ListView
import javafx.scene.control.TextField
import javafx.scene.control.Tooltip
import javafx.scene.image.ImageView
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.GridPane
import javafx.scene.layout.Priority

/**
 * Manages a [ListView] of all [SoundFile]s, with the ability to filter items based on the input of a search field.
 *
 *  @param selection Items to be initially selected.
 */
class SoundFileTracker(selection: List<SoundFile>) {
    private val allItems = SoundFiles.getAll()
    private val soundToggles: Map<SoundFile, BooleanProperty> = allItems.associateWith { SimpleBooleanProperty(it in selection) }
    private val searchResults = FXCollections.observableArrayList(allItems).apply {
        FXCollections.sort(this) { lhs, rhs ->
            val lhsSelected = soundToggles[lhs]?.value ?: false
            val rhsSelected = soundToggles[rhs]?.value ?: false
            when {
                lhsSelected == rhsSelected -> 0
                lhsSelected -> -1
                else -> 1
            }
        }
    }

    fun createSearchField(): TextField {
        return TextField().apply {
            promptText = PROMPT_SEARCH
            textProperty().addListener { _, _, newValue -> filter(newValue) }
        }
    }

    fun createListView(): ListView<SoundFile> {
        return ListView<SoundFile>(searchResults).apply {
            setCellFactory { CheckBoxWithButtonCell { soundToggles[it] } }
        }
    }

    fun getSelection(): List<SoundFile> {
        return soundToggles.filterValues { it.value }.keys.toList()
    }

    private fun filter(query: String) {
        searchResults.clear()
        searchResults.addAll(allItems.filter { it.name.contains(query.trim(), ignoreCase = true) })
    }

    private class CheckBoxWithButtonCell(private val getSelectedProperty: (SoundFile?) -> BooleanProperty?) : ListCell<SoundFile>() {
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

        override fun updateItem(item: SoundFile?, empty: Boolean) {
            super.updateItem(item, empty)
            if (empty) {
                graphic = null
                return
            }
            graphic = container
            checkBox.text = item?.name
            button.setOnAction { item?.play() }
            button.tooltip = Tooltip(TOOLTIP_PLAY_LOCALLY)

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
