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
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.test.runBlockingTest
import okhttp3.ResponseBody
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@ExperimentalCoroutinesApi
class PlaySoundRepositoryImplTest {
    @MockK
    private lateinit var service: PlaySoundService
    private lateinit var repository: PlaySoundRepositoryImpl

    @BeforeEach
    internal fun setUp() {
        MockKAnnotations.init(this)
        repository = PlaySoundRepositoryImpl(service, Dispatchers.Unconfined)
    }

    @Test
    internal fun testListSoundTriggers() = runBlockingTest {
        val actual = repository.listSoundTriggers().single()

        assertEquals(expectedTriggers, actual)
    }

    @Test
    internal fun testListSounds_Success_ReturnsServiceResponse() = runBlockingTest {
        val sounds = mapOf("a" to "b")
        coEvery { service.listSoundBites() } returns sounds

        val actual = repository.listSounds()

        assertTrue(actual.isSuccess)
        assertSame(sounds, actual.getOrThrow())
    }

    @Test
    internal fun testListSounds_Exception_ReturnsFailureResult() = runBlockingTest {
        coEvery { service.listSoundBites() } throws RuntimeException()

        val actual = repository.listSounds()

        assertTrue(actual.isFailure)
    }

    @Test
    internal fun testDownloadSound_Success_ReturnsServiceResponse() = runBlockingTest {
        val responseBody = mockk<ResponseBody>()
        coEvery { service.downloadSoundBite("roons.mp3") } returns responseBody

        val actual = repository.downloadSound("roons.mp3")

        assertTrue(actual.isSuccess)
        assertSame(responseBody, actual.getOrThrow())
    }

    @Test
    internal fun testDownloadSound_Exception_ReturnsFailureResult() = runBlockingTest {
        coEvery { service.downloadSoundBite("roons.mp3") } throws RuntimeException()

        val actual = repository.downloadSound("roons.mp3")

        assertTrue(actual.isFailure)
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