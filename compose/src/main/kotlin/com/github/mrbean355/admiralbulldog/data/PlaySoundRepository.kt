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

package com.github.mrbean355.admiralbulldog.data

import com.github.mrbean355.admiralbulldog.sounds.triggers.OnBountyRunesSpawn
import com.github.mrbean355.admiralbulldog.sounds.triggers.OnDeath
import com.github.mrbean355.admiralbulldog.sounds.triggers.OnDefeat
import com.github.mrbean355.admiralbulldog.sounds.triggers.OnHeal
import com.github.mrbean355.admiralbulldog.sounds.triggers.OnKill
import com.github.mrbean355.admiralbulldog.sounds.triggers.OnMatchStart
import com.github.mrbean355.admiralbulldog.sounds.triggers.OnMidasReady
import com.github.mrbean355.admiralbulldog.sounds.triggers.OnRespawn
import com.github.mrbean355.admiralbulldog.sounds.triggers.OnSmoked
import com.github.mrbean355.admiralbulldog.sounds.triggers.OnVictory
import com.github.mrbean355.admiralbulldog.sounds.triggers.Periodically
import com.github.mrbean355.admiralbulldog.sounds.triggers.SoundTrigger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

interface PlaySoundRepository {

    fun listSoundTriggers(): Flow<List<SoundTrigger>>

}

fun PlaySoundRepository(): PlaySoundRepository = PlaySoundRepositoryImpl()

class PlaySoundRepositoryImpl : PlaySoundRepository {

    override fun listSoundTriggers() = flowOf(
        listOf(
            OnBountyRunesSpawn,
            OnKill,
            OnDeath,
            OnRespawn,
            OnHeal,
            OnSmoked,
            OnMidasReady,
            OnMatchStart,
            OnVictory,
            OnDefeat,
            Periodically
        )
    )
}