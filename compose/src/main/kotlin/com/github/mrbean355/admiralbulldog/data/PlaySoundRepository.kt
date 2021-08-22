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

import com.github.mrbean355.admiralbulldog.core.verifyChecksum
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
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File

private const val SOUNDS_DIR = "sounds_v2"

interface PlaySoundRepository {

    fun listSoundTriggers(): Flow<List<SoundTrigger>>

    /**
     * Download the latest sound bites.
     * Re-downloads missing or modified sounds.
     * Deletes the sounds that don't exist remotely.
     */
    fun downloadAllSounds(): Flow<SyncState>

}

fun PlaySoundRepository(): PlaySoundRepository =
    PlaySoundRepositoryImpl(PlaySoundService.Instance, Dispatchers.IO)

class PlaySoundRepositoryImpl(
    private val service: PlaySoundService,
    private val dispatcher: CoroutineDispatcher
) : PlaySoundRepository {

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

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun downloadAllSounds() = channelFlow {
        val remoteSounds = listSounds().getOrNull()
        if (remoteSounds == null) {
            send(SyncState.Failed)
            close()
            return@channelFlow
        }
        send(SyncState.Start(remoteSounds.size))

        val toDelete = getLocalSounds().filter { it !in remoteSounds.keys }
        var current = 0
        val mutex = Mutex()

        coroutineScope {
            remoteSounds.forEach { (soundName, checksum) ->
                launch {
                    val output = File(SOUNDS_DIR, soundName)
                    val existsLocally = soundBiteExistsLocally(soundName)
                    if (!existsLocally || !output.verifyChecksum(checksum)) {
                        downloadSound(soundName).onSuccess { body ->
                            body.byteStream().use { input ->
                                output.outputStream().use(input::copyTo)
                            }
                        }
                    }
                    mutex.withLock {
                        send(SyncState.Progress(++current))
                    }
                }
            }
        }

        toDelete.forEach {
            File(SOUNDS_DIR, it).delete()
        }

        send(SyncState.Complete)
        close()
    }.flowOn(dispatcher)

    private suspend fun listSounds() = withContext(dispatcher) {
        runCatching { service.listSoundBites() }
    }

    private suspend fun downloadSound(name: String) = withContext(dispatcher) {
        runCatching { service.downloadSoundBite(name) }
    }

    private fun getLocalSounds(): List<String> {
        val root = File(SOUNDS_DIR)
        if (!root.exists()) {
            root.mkdirs()
            return emptyList()
        }
        return root.list()?.toList().orEmpty()
    }

    private fun soundBiteExistsLocally(name: String): Boolean {
        return File("$SOUNDS_DIR/$name").exists()
    }
}