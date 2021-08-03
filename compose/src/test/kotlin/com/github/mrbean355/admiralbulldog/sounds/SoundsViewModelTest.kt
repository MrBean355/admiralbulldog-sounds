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

import com.github.mrbean355.admiralbulldog.data.PlaySoundRepository
import com.github.mrbean355.admiralbulldog.sounds.triggers.SoundTrigger
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.flow.Flow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class SoundsViewModelTest {
    @MockK
    private lateinit var playSoundRepository: PlaySoundRepository

    @MockK
    private lateinit var mockFlow: Flow<List<SoundTrigger>>
    private lateinit var viewModel: SoundsViewModel

    @BeforeEach
    internal fun setUp() {
        MockKAnnotations.init(this)
        every { playSoundRepository.listSoundTriggers() } returns mockFlow
        viewModel = SoundsViewModel(playSoundRepository)
    }

    @Test
    internal fun testTriggers() {
        val actual = viewModel.triggers

        assertSame(mockFlow, actual)
    }

    @Test
    internal fun testDescription() {
        val actual = viewModel.description(mockk())

        assertEquals("Enabled; 5 sounds chosen", actual)
    }
}