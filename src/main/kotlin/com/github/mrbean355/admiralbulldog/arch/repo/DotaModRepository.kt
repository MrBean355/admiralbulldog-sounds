/*
 * Copyright 2022 Michael Johnston
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

package com.github.mrbean355.admiralbulldog.arch.repo

import com.github.mrbean355.admiralbulldog.arch.DotaMod
import com.github.mrbean355.admiralbulldog.arch.ServiceResponse
import com.github.mrbean355.admiralbulldog.arch.service.DiscordBotService
import com.github.mrbean355.admiralbulldog.arch.toServiceResponse
import com.github.mrbean355.admiralbulldog.arch.verifyChecksum
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import java.io.File

private const val MOD_DIRECTORY = "game/dota_bulldog"
private const val VPK_FILE = "pak%02d_dir.vpk"
private val VpkFilePattern = """pak(\d+)_dir\.vpk""".toRegex()

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
        val enabledMods = ConfigPersistence.getEnabledMods().sorted()
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
                .filter { !verifyModHash(it, enabledMods.indexOf(it.key) + 1) }
        )
    }

    /**
     * Download the latest version of each given [DotaMod].
     * Remove mods from the config file that aren't in the list.
     */
    suspend fun installMods(mods: Collection<DotaMod>): Boolean = withContext(IO) {
        val allSucceeded = updateMods(mods)
        ConfigPersistence.setEnabledMods(mods)
        val lastSequence = mods.size

        File(ConfigPersistence.getDotaPath(), MOD_DIRECTORY).listFiles()?.forEach { file ->
            VpkFilePattern.matchEntire(file.name)?.let { result ->
                val sequence = result.groupValues[1].toInt()
                if (sequence > lastSequence) {
                    file.delete()
                }
            }
        }
        allSucceeded
    }

    /** Clear all chosen mods and delete them from disk. */
    fun uninstallAllMods() {
        ConfigPersistence.setEnabledMods(emptyList())
        try {
            File(ConfigPersistence.getDotaPath(), MOD_DIRECTORY).deleteRecursively()
        } catch (t: Throwable) {
            logger.warn("Failed to delete mod directory", t)
        }
    }

    /** Download the latest version of each given [DotaMod]. */
    suspend fun updateMods(mods: Collection<DotaMod>): Boolean = withContext(IO) {
        val mapping = mods.sortedBy { it.key }.withIndex()
        var allSucceeded = true
        mapping.forEach { (index, mod) ->
            if (verifyModHash(mod, index + 1)) {
                ConfigPersistence.enableMod(mod)
            } else {
                allSucceeded = allSucceeded && downloadMod(mod, index + 1).body ?: false
            }
        }
        allSucceeded
    }

    private suspend fun downloadMod(mod: DotaMod, sequence: Int): ServiceResponse<Boolean> {
        return try {
            val response = DiscordBotService.INSTANCE.downloadFile(mod.downloadUrl).toServiceResponse()
            if (response.isSuccessful()) {
                val parent = File(ConfigPersistence.getDotaPath(), MOD_DIRECTORY).also {
                    it.mkdirs()
                }
                val target = File(parent, VPK_FILE.format(sequence))
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
     * Check whether the hash of the locally downloaded mod's VPK is the same as the hash on the server.
     * @return `true` if the hashes are equal.
     */
    private fun verifyModHash(mod: DotaMod, sequence: Int): Boolean {
        val parent = File(ConfigPersistence.getDotaPath(), MOD_DIRECTORY)
        if (!parent.exists()) {
            return false
        }
        val vpk = File(parent, VPK_FILE.format(sequence))
        return if (vpk.exists()) {
            vpk.verifyChecksum(mod.hash)
        } else {
            false
        }
    }
}