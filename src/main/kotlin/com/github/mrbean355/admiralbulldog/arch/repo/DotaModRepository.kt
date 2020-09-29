package com.github.mrbean355.admiralbulldog.arch.repo

import com.github.mrbean355.admiralbulldog.arch.DotaMod
import com.github.mrbean355.admiralbulldog.arch.ServiceResponse
import com.github.mrbean355.admiralbulldog.arch.service.DiscordBotService
import com.github.mrbean355.admiralbulldog.arch.toServiceResponse
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
    suspend fun checkForUpdates(): Collection<DotaMod> = withContext(IO) {
        if (!ConfigPersistence.hasEnabledMods()) {
            return@withContext emptyList()
        }
        val response = listMods()
        val body = response.body
        if (!response.isSuccessful() || body == null) {
            return@withContext emptyList()
        }
        body.filter {
            it.hash != ConfigPersistence.getEnabledModHash(it)
        }
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
        Dota2GameInfo.setIncludedModDirectories(mods.map { it.id })
        allSucceeded
    }

    /**
     * Download the latest version of each given [DotaMod].
     * Once complete, update the mod's hash in the config file.
     */
    suspend fun updateMods(mods: Collection<DotaMod>): Boolean = withContext(IO) {
        var allSucceeded = true
        mods.forEach { mod ->
            val current = ConfigPersistence.getEnabledModHash(mod)
            if (current != mod.hash) {
                allSucceeded = allSucceeded && downloadMod(mod).body ?: false
            }
        }
        allSucceeded
    }

    private suspend fun downloadMod(mod: DotaMod): ServiceResponse<Boolean> {
        return try {
            val response = DiscordBotService.INSTANCE.downloadFile(mod.downloadUrl).toServiceResponse()
            if (response.isSuccessful()) {
                val parent = File(ConfigPersistence.getDotaPath(), MOD_DIRECTORY.format(mod.id)).also {
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
}