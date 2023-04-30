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
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import com.github.mrbean355.admiralbulldog.triggers.SoundTriggerType
import tornadofx.Fragment

class ChooseSoundFilesScreen : Fragment() {
    private val type by param<SoundTriggerType>()
    private val tracker = SoundBiteTracker(ConfigPersistence.getSoundsForType(type))

    override val root = tracker.createUi(this, ::onSaveClicked)

    init {
        title = type.friendlyName
    }

    private fun onSaveClicked(selection: List<SoundBite>) {
        ConfigPersistence.saveSoundsForType(type, selection)
        close()
    }

    companion object {

        fun params(type: SoundTriggerType): Map<String, Any?> {
            return mapOf("type" to type)
        }
    }
}