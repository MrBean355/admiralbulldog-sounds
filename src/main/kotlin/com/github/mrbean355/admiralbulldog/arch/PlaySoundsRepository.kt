package com.github.mrbean355.admiralbulldog.arch

import java.io.FileOutputStream

class PlaySoundsRepository {

    suspend fun listRemoteSounds(): ServiceResponse<List<RemoteSoundBite>> {
        val response = callService { PlaySoundsService.INSTANCE.getHtml() }
        val responseBody = response.body()
        if (!response.isSuccessful || responseBody == null) {
            return ServiceResponse.Error(response.code())
        }
        val html = responseBody.charStream().readText()
        val blocks = html.split("<tr>")
                .drop(2)

        return ServiceResponse.Success(blocks.map { block ->
            val friendlyName = block.split("<td>")[1].split("</td>").first()
            val url = block.split("data-link=\"")[1].split("\"").first()
            val fileExtension = url.substringAfterLast('.', missingDelimiterValue = "")
            val fileName = "$friendlyName.$fileExtension"
            RemoteSoundBite(fileName, url)
        })
    }

    suspend fun downloadRemoteSound(remoteSoundBite: RemoteSoundBite, destination: String) {
        val response = callService { NuulsService.INSTANCE.get(remoteSoundBite.url) }
        val responseBody = response.body()
        if (response.isSuccessful && responseBody != null) {
            FileOutputStream("$destination/${remoteSoundBite.fileName}").use {
                responseBody.byteStream().copyTo(it)
            }
            return
        }
        val fallbackResponse = callService { DiscordBotService.INSTANCE.downloadSoundBite(remoteSoundBite.fileName) }
        val fallbackResponseBody = fallbackResponse.body()
        if (fallbackResponse.isSuccessful && fallbackResponseBody != null) {
            FileOutputStream("$destination/${remoteSoundBite.fileName}").use {
                fallbackResponseBody.byteStream().copyTo(it)
            }
        }
    }
}