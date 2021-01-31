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

package com.github.mrbean355.admiralbulldog.arch.repo

import com.github.mrbean355.admiralbulldog.arch.DotaMod
import com.github.mrbean355.admiralbulldog.arch.ServiceResponse
import com.github.mrbean355.admiralbulldog.arch.service.DiscordBotService
import com.github.mrbean355.admiralbulldog.arch.toServiceResponse
import com.github.mrbean355.admiralbulldog.arch.verifyChecksum
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import com.github.mrbean355.admiralbulldog.persistence.Dota2GameInfo
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import java.io.File

/** Directory within the Dota root to place mods. */
private const val MOD_DIRECTORY = "game/%s"
private const val VPK_FILE = "pak01_dir.vpk"

class DotaModRepository {
    private val logger = LoggerFactory.getLogger(DotaModRepository::class.java)

    suspend fun listMods(): ServiceResponse<List<DotaMod>> = withContext(IO) {
        try {
            DiscordBotService.INSTANCE.listMods()
                .toServiceResponse()
        } catch (t: Throwable) {
            logger.error("Error getting mods list", t)
            ServiceResponse.Exception()
        }
    }

    /**
     * Check for a newer version of each mod that has been enabled.
     * @return mods that can be updated.
     */
    suspend fun checkForUpdates(): ServiceResponse<Collection<DotaMod>> = withContext(IO) {
        val enabledMods = ConfigPersistence.getEnabledMods()
        if (enabledMods.isEmpty()) {
            return@withContext ServiceResponse.Success(emptyList())
        }
        val response = listMods()
        val body = response.body
        if (!response.isSuccessful() || body == null) {
            return@withContext ServiceResponse.Error(response.statusCode)
        }
        ConfigPersistence.setModLastUpdateToNow()
        ServiceResponse.Success(
            body.filter { it.key in enabledMods }
                .filter { !verifyModHash(it) }
        )
    }

    /**
     * Download the latest version of each given [DotaMod].
     * Once complete, update the mod's hash in the config file.
     * Remove mods from the config file that aren't in the list.
     * Update the game info file to include only these mods.
     */
    suspend fun installMods(mods: Collection<DotaMod>): Boolean = withContext(IO) {
        val allSucceeded = updateMods(mods)
        ConfigPersistence.disableOtherMods(mods)
        Dota2GameInfo.setIncludedModDirectories(mods.map { it.key })
        allSucceeded
    }

    /**
     * Download the latest version of each given [DotaMod].
     * Once complete, update the mod's hash in the config file.
     */
    suspend fun updateMods(mods: Collection<DotaMod>): Boolean = withContext(IO) {
        var allSucceeded = true
        mods.forEach { mod ->
            if (verifyModHash(mod)) {
                ConfigPersistence.enableMod(mod)
            } else {
                allSucceeded = allSucceeded && downloadMod(mod).body ?: false
            }
        }
        allSucceeded
    }

    private suspend fun downloadMod(mod: DotaMod): ServiceResponse<Boolean> {
        return try {
            val response = DiscordBotService.INSTANCE.downloadFile(mod.downloadUrl).toServiceResponse()
            if (response.isSuccessful()) {
                val parent = File(ConfigPersistence.getDotaPath(), MOD_DIRECTORY.format(mod.key)).also {
                    it.mkdirs()
                }
                val target = File(parent, VPK_FILE)
                target.outputStream().use { output ->
                    response.body?.byteStream()?.use { input ->
                        input.copyTo(output)
                        ConfigPersistence.enableMod(mod)
                        return ServiceResponse.Success(true)
                    }
                }
            }
            ServiceResponse.Success(false)
        } catch (t: Throwable) {
            logger.error("Error downloading mod", t)
            ServiceResponse.Exception()
        }
    }

    /**
     * Check whether the hash of the locally download mod's VPK is the same as the hash on the server.
     * @return `true` if the hashes are equal.
     */
    private fun verifyModHash(mod: DotaMod): Boolean {
        val parent = File(ConfigPersistence.getDotaPath(), MOD_DIRECTORY.format(mod.key))
        if (!parent.exists()) {
            return false
        }
        val vpk = File(parent, VPK_FILE)
        return if (vpk.exists()) {
            vpk.verifyChecksum(mod.hash)
        } else {
            false
        }
    }
}