package com.github.mrbean355.admiralbulldog.sounds

import com.github.mrbean355.admiralbulldog.assets.SoundBite
import com.github.mrbean355.admiralbulldog.assets.SoundBites
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.common.useCheckBoxWithButton
import javafx.beans.property.BooleanProperty
import javafx.collections.transformation.SortedList
import javafx.scene.control.ListView
import javafx.scene.control.TextField
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
            useCheckBoxWithButton(
                    stringConverter = { it.name },
                    getSelectedProperty = { soundToggles.getValue(it) },
                    onButtonClicked = { it.play() }
            )
        }
    }

    fun getSelection(): List<SoundBite> {
        return soundToggles.filterValues { it.value }.keys.toList()
    }

    private fun filter(query: String) {
        searchResults.setAll(allItems.filter { it.name.contains(query.trim(), ignoreCase = true) })
    }
}
