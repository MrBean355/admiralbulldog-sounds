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

package com.github.mrbean355.admiralbulldog.sounds.download

import com.github.mrbean355.admiralbulldog.data.PlaySoundRepository
import com.github.mrbean355.admiralbulldog.data.SyncState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DownloadSoundsViewModel(
    private val repository: PlaySoundRepository,
    coroutineScope: CoroutineScope
) {
    private val _total = MutableStateFlow(0)
    private val _current = MutableStateFlow(0)

    val total: StateFlow<Int> get() = _total
    val current: StateFlow<Int> get() = _current

    constructor(coroutineScope: CoroutineScope) : this(PlaySoundRepository(), coroutineScope)

    init {
        coroutineScope.launch {
            repository.downloadAllSounds().collectLatest {
                when (it) {
                    SyncState.Failed -> TODO()
                    is SyncState.Start -> _total.value = it.total
                    is SyncState.Progress -> _current.value = it.current
                    is SyncState.Complete -> Unit
                }
            }
        }
    }
}