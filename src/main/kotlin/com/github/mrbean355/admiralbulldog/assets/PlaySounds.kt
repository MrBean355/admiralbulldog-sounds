package com.github.mrbean355.admiralbulldog.assets

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import java.io.File
import java.io.FileOutputStream

private const val HOST_CHATBOT = "http://chatbot.admiralbulldog.live/"
private const val HOST_NUULS = "https://i.nuuls.com/"

object PlaySounds {

    /** Scrape the PlaySounds web page and collect a list of sound file names and URLs. */
    suspend fun listRemoteFiles(): List<RemoteSoundFile> {
        val response = createChatBotService().getHtml()
        val responseBody = response.body()
        if (!response.isSuccessful || responseBody == null) {
            throw RuntimeException("Unable to download HTML, code=${response.code()}, body length=${responseBody?.contentLength()}")
        }
        return withContext(IO) {
            val html = String(responseBody.bytes())
            val blocks = html.split("<tr>")
                    .drop(2)

            blocks.map { block ->
                val friendlyName = block.split("<td>")[1].split("</td>").first()
                val url = block.split("data-link=\"")[1].split("\"").first()
                val fileExtension = url.substringAfterLast('.', missingDelimiterValue = "")
                val fileName = "$friendlyName.$fileExtension"
                RemoteSoundFile(fileName, url)
            }
        }
    }

    /** Download the given sound to the given destination. */
    suspend fun downloadFile(file: RemoteSoundFile) {
        val response = createNuulsService().get(file.url.removePrefix(HOST_NUULS))
        val responseBody = response.body()
        if (!response.isSuccessful || responseBody == null) {
            throw RuntimeException("Unable to download $file, code=${response.code()}, body length=${responseBody?.contentLength()}")
        }
        val stream = responseBody.byteStream()
        val output = FileOutputStream("$SOUNDS_PATH/${file.fileName}")
        val buffer = ByteArray(4096)
        withContext(IO) {
            while (true) {
                val read = stream.read(buffer)
                if (read == -1) {
                    break
                }
                output.write(buffer, 0, read)
            }
            output.close()
            stream.close()
        }
    }

    private fun createChatBotService(): PlaySoundsService {
        return Retrofit.Builder()
                .baseUrl(HOST_CHATBOT)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build()
                .create(PlaySoundsService::class.java)
    }

    private fun createNuulsService(): NuulsService {
        return Retrofit.Builder()
                .baseUrl(HOST_NUULS)
                .build()
                .create(NuulsService::class.java)
    }

    data class RemoteSoundFile(val fileName: String, val url: String) {

        fun existsLocally(): Boolean {
            return File("${SOUNDS_PATH}/$fileName").exists()
        }
    }

    private interface PlaySoundsService {
        @GET("playsounds")
        suspend fun getHtml(): Response<ResponseBody>
    }

    private interface NuulsService {
        @GET("{name}")
        suspend fun get(@retrofit2.http.Path("name") name: String): Response<ResponseBody>
    }
}

