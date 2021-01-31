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

package com.github.mrbean355.admiralbulldog.sounds.manager

import com.github.mrbean355.admiralbulldog.assets.SoundBite
import com.github.mrbean355.admiralbulldog.assets.SoundBites
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import com.github.mrbean355.admiralbulldog.triggers.SoundTriggerType
import javafx.beans.property.BooleanProperty
import javafx.beans.property.StringProperty
import javafx.collections.ObservableList
import tornadofx.ViewModel
import tornadofx.booleanProperty
import tornadofx.onChange
import tornadofx.stringProperty
import tornadofx.toObservable

class SoundManagerViewModel : ViewModel() {
    val searchQuery: StringProperty = stringProperty()
    val items: ObservableList<SoundBite> = SoundBites.getAll().toObservable()

    init {
        searchQuery.onChange { text ->
            val query = text.orEmpty().trim()
            items.setAll(SoundBites.getAll().filter { it.name.contains(query, ignoreCase = true) })
        }
    }

    fun getCellProperty(type: SoundTriggerType, index: Int): BooleanProperty {
        val sound = items[index]
        return booleanProperty(ConfigPersistence.isSoundSelectedForType(type, sound)).apply {
            onChange { ConfigPersistence.setSoundSelectedForType(type, sound, it) }
        }
    }
}