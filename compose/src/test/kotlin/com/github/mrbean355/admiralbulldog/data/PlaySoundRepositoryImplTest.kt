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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@ExperimentalCoroutinesApi
class PlaySoundRepositoryImplTest {
    private lateinit var repository: PlaySoundRepositoryImpl

    @BeforeEach
    internal fun setUp() {
        repository = PlaySoundRepositoryImpl()
    }

    @Test
    internal fun testListSoundTriggers() = runBlockingTest {
        val actual = repository.listSoundTriggers().single()

        assertEquals(expectedTriggers, actual)
    }

    private val expectedTriggers = listOf(
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
}