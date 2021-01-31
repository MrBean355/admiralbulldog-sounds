/*
 * Copyright 2021 Michael Johnston
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.mrbean355.admiralbulldog.sounds

import com.github.mrbean355.admiralbulldog.assets.SoundBite
import com.github.mrbean355.admiralbulldog.assets.SoundBites
import com.github.mrbean355.admiralbulldog.common.PlayIcon
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
                    buttonImage = PlayIcon(),
                    buttonTooltip = getString("tooltip_play_locally"),
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
