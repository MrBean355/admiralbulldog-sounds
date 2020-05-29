package com.github.mrbean355.admiralbulldog.arch

class DotaModRepository {

    suspend fun listMods(): ServiceResponse<List<DotaMod>> {
        return DiscordBotService.INSTANCE.listMods().toServiceResponse()
    }
}