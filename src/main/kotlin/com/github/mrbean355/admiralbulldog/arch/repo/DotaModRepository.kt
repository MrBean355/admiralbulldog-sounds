package com.github.mrbean355.admiralbulldog.arch.repo

import com.github.mrbean355.admiralbulldog.arch.DotaMod
import com.github.mrbean355.admiralbulldog.arch.ServiceResponse
import com.github.mrbean355.admiralbulldog.arch.service.DiscordBotService
import com.github.mrbean355.admiralbulldog.arch.toServiceResponse
import org.slf4j.LoggerFactory

class DotaModRepository {
    private val logger = LoggerFactory.getLogger(DotaModRepository::class.java)

    suspend fun listMods(): ServiceResponse<List<DotaMod>> {
        return try {
            DiscordBotService.INSTANCE.listMods()
                    .toServiceResponse()
        } catch (t: Throwable) {
            logger.error("Error getting mods list", t)
            ServiceResponse.Exception()
        }
    }
}