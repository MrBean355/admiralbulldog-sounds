package com.github.mrbean355.admiralbulldog.ui

import com.github.mrbean355.admiralbulldog.assets.SoundBite
import com.github.mrbean355.admiralbulldog.assets.SoundBites
import com.github.mrbean355.admiralbulldog.playIcon
import javafx.beans.property.BooleanProperty
import javafx.collections.transformation.SortedList
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
import tornadofx.booleanProperty
import tornadofx.onChange
import tornadofx.toObservable

/**
 * Manages a [ListView] of all [SoundBite]s, with the ability to filter items based on the input of a search field.
 *
 *  @param selection Items to be initially selected.
 */
class SoundBiteTracker(selection: List<SoundBite>) {
    private val allItems = SoundBites.getAll()
    private val soundToggles: Map<SoundBite, BooleanProperty> = allItems.associateWith { booleanProperty(it in selection) }
    private val searchResults = allItems.toObservable()

    fun createSearchField(): TextField {
        return TextField().apply {
            promptText = getString("prompt_search")
            textProperty().onChange { filter(it.orEmpty()) }
        }
    }

    fun createListView(): ListView<SoundBite> {
        val sortedList = SortedList(searchResults, Comparator { lhs, rhs ->
            val lhsSelected = soundToggles[lhs]?.value ?: false
            val rhsSelected = soundToggles[rhs]?.value ?: false
            when {
                lhsSelected == rhsSelected -> 0
                lhsSelected -> -1
                else -> 1
            }
        })
        return ListView(sortedList).apply {
            setCellFactory { CheckBoxWithButtonCell { soundToggles[it] } }
        }
    }

    fun getSelection(): List<SoundBite> {
        return soundToggles.filterValues { it.value }.keys.toList()
    }

    private fun filter(query: String) {
        searchResults.setAll(allItems.filter { it.name.contains(query.trim(), ignoreCase = true) })
    }

    private class CheckBoxWithButtonCell(private val getSelectedProperty: (SoundBite?) -> BooleanProperty?) : ListCell<SoundBite>() {
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
            if (empty) {
                graphic = null
                return
            }
            graphic = container
            checkBox.text = item?.name
            button.setOnAction { item?.play() }
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
}
