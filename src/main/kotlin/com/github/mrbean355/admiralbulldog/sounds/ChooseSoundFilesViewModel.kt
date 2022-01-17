/*
 * Copyright 2022 Michael Johnston
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
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import com.github.mrbean355.admiralbulldog.triggers.SoundTriggerType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class ChooseSoundFilesViewModel(
    private val triggerType: SoundTriggerType
) {
    private val flows = mutableMapOf<SoundBite, MutableStateFlow<Boolean>>()
    private val allSounds = SoundBites.getAll().sortedWith(soundBiteComparator())

    val searchQuery = MutableStateFlow("")
    val items = searchQuery.map { query ->
        allSounds.filter { it.name.contains(query, ignoreCase = true) }
    }.flowOn(Dispatchers.Default)

    fun isSelected(soundBite: SoundBite): Flow<Boolean> = flows.getOrPut(soundBite) {
        MutableStateFlow(ConfigPersistence.isSoundSelectedForType(triggerType, soundBite))
    }

    fun onSelectionChanged(soundBite: SoundBite, isSelected: Boolean) {
        ConfigPersistence.setSoundSelectedForType(triggerType, soundBite, isSelected)
        flows[soundBite]?.value = isSelected
    }

    fun onSearch(query: String) {
        searchQuery.value = query.trim()
    }

    private fun soundBiteComparator(): (SoundBite, SoundBite) -> Int = { lhs, rhs ->
        val lhsSelected = ConfigPersistence.isSoundSelectedForType(triggerType, lhs)
        val rhsSelected = ConfigPersistence.isSoundSelectedForType(triggerType, rhs)
        when {
            lhsSelected == rhsSelected -> lhs.name.compareTo(rhs.name, ignoreCase = true)
            lhsSelected -> -1
            else -> 1
        }
    }
}