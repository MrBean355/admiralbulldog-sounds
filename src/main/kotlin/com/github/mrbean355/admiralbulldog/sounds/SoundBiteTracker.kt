/*
 * Copyright 2023 Michael Johnston
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

package com.github.mrbean355.admiralbulldog.sounds

import com.github.mrbean355.admiralbulldog.assets.SoundBite
import com.github.mrbean355.admiralbulldog.assets.SoundBites
import com.github.mrbean355.admiralbulldog.common.PADDING_MEDIUM
import com.github.mrbean355.admiralbulldog.common.PADDING_SMALL
import com.github.mrbean355.admiralbulldog.common.PlayIcon
import com.github.mrbean355.admiralbulldog.common.WINDOW_WIDTH
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.common.useCheckBoxWithButton
import javafx.beans.property.BooleanProperty
import javafx.collections.transformation.SortedList
import javafx.event.EventTarget
import javafx.scene.Parent
import javafx.scene.control.ButtonBar
import javafx.scene.control.ListView
import tornadofx.action
import tornadofx.booleanProperty
import tornadofx.button
import tornadofx.buttonbar
import tornadofx.hbox
import tornadofx.hyperlink
import tornadofx.label
import tornadofx.listview
import tornadofx.onChange
import tornadofx.paddingAll
import tornadofx.paddingRight
import tornadofx.paddingTop
import tornadofx.textfield
import tornadofx.toObservable
import tornadofx.vbox

/**
 * Manages a [ListView] of all [SoundBite]s, with the ability to filter items based on the input of a search field.
 * Provides buttons to select all and select none.
 *
 *  @param selection Items to be initially selected.
 */
class SoundBiteTracker(selection: List<SoundBite>) {
    private val allItems = SoundBites.getAll()
    private val soundToggles: Map<SoundBite, BooleanProperty> = allItems.associateWith { booleanProperty(it in selection) }
    private val searchResults = allItems.toObservable()

    /**
     * @param target parent to attach the UI to.
     * @param onSaveClicked called with the current selection when save is clicked.
     */
    fun createUi(
        target: EventTarget,
        onSaveClicked: (List<SoundBite>) -> Unit
    ): Parent = with(target) {
        vbox(spacing = PADDING_SMALL) {
            paddingAll = PADDING_MEDIUM
            prefWidth = WINDOW_WIDTH
            textfield {
                promptText = getString("prompt_search")
                textProperty().onChange { filter(it.orEmpty()) }
            }
            val sortedList = SortedList(searchResults) { lhs, rhs ->
                val lhsSelected = soundToggles[lhs]?.value ?: false
                val rhsSelected = soundToggles[rhs]?.value ?: false
                when {
                    lhsSelected == rhsSelected -> 0
                    lhsSelected -> -1
                    else -> 1
                }
            }
            listview(sortedList).apply {
                useCheckBoxWithButton(
                    buttonImage = PlayIcon(),
                    buttonTooltip = getString("tooltip_play_locally"),
                    stringConverter = { it.name },
                    getSelectedProperty = { soundToggles.getValue(it) },
                    onButtonClicked = { it.play() }
                )
            }
            hbox {
                label(getString("label_select")) {
                    paddingTop = 3
                    paddingRight = 4
                }
                hyperlink(getString("btn_select_all")) {
                    action { selectAll() }
                }
                hyperlink(getString("btn_deselect_all")) {
                    action { selectNone() }
                }
            }
            buttonbar {
                button(getString("btn_save"), ButtonBar.ButtonData.OK_DONE) {
                    action { onSaveClicked(getSelection()) }
                }
            }
        }
    }

    private fun selectAll() {
        soundToggles.keys.forEach {
            soundToggles.getValue(it).set(true)
        }
    }

    private fun selectNone() {
        soundToggles.keys.forEach {
            soundToggles.getValue(it).set(false)
        }
    }

    private fun getSelection(): List<SoundBite> {
        return soundToggles.filterValues { it.value }.keys.toList()
    }

    private fun filter(query: String) {
        searchResults.setAll(allItems.filter { it.name.contains(query.trim(), ignoreCase = true) })
    }
}
