package com.github.mrbean355.admiralbulldog.assets

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit

private const val HOST_CHATBOT = "http://chatbot.admiralbulldog.live/"
private const val HOST_NUULS = "https://i.nuuls.com/"

class ServiceResponse<T>(val success: Boolean, val body: T? = null)

/**
 * Queries the PlaySounds page to get the latest sounds.
 */
object PlaySounds {

    /** Scrape the PlaySounds web page and collect a list of sound file names and URLs. */
    suspend fun listRemoteSoundBytes(): ServiceResponse<List<RemoteSoundByte>> {
        val response = createChatBotService().getHtml()
        val responseBody = response.body()
        if (!response.isSuccessful || responseBody == null) {
            return ServiceResponse(false)
        }
        return withContext(IO) {
            val html = String(responseBody.bytes())
            val blocks = html.split("<tr>")
                    .drop(2)

            ServiceResponse(true, blocks.map { block ->
                val friendlyName = block.split("<td>")[1].split("</td>").first()
                val url = block.split("data-link=\"")[1].split("\"").first()
                val fileExtension = url.substringAfterLast('.', missingDelimiterValue = "")
                val fileName = "$friendlyName.$fileExtension"
                RemoteSoundByte(fileName, url)
            })
        }
    }

    /** Download the given sound file to the given destination. */
    suspend fun downloadSoundByte(remoteSoundByte: RemoteSoundByte, destination: String) {
        val response = createNuulsService().get(remoteSoundByte.url.removePrefix(HOST_NUULS))
        val responseBody = response.body()
        if (!response.isSuccessful || responseBody == null) {
            throw RuntimeException("Unable to download $remoteSoundByte, response=$response")
        }
        val stream = responseBody.byteStream()
        val output = FileOutputStream("$destination/${remoteSoundByte.fileName}")
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

    /** A sound on the PlaySounds page. */
    data class RemoteSoundByte(
            /** Remote file name with extension. */
            val fileName: String,
            /** URL where the file is hosted. */
            val url: String)

    private fun createChatBotService(): PlaySoundsService {
        return Retrofit.Builder()
                .client(OkHttpClient.Builder()
                        .callTimeout(10, TimeUnit.SECONDS)
                        .build())
                .baseUrl(HOST_CHATBOT)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build()
                .create(PlaySoundsService::class.java)
    }

    private fun createNuulsService(): NuulsService {
        return Retrofit.Builder()
                .client(OkHttpClient.Builder()
                        .callTimeout(10, TimeUnit.SECONDS)
                        .build())
                .baseUrl(HOST_NUULS)
                .build()
                .create(NuulsService::class.java)
    }

    private interface PlaySoundsService {
        @GET("playsounds")
        suspend fun getHtml(): Response<ResponseBody>
    }

    private interface NuulsService {
        @GET("{name}")
        suspend fun get(@Path("name") name: String): Response<ResponseBody>
    }
}

