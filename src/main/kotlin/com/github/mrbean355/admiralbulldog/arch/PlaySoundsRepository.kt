package com.github.mrbean355.admiralbulldog.arch

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit

private const val HOST_CHATBOT = "http://chatbot.admiralbulldog.live/"

class PlaySoundsRepository {
    private val playSoundsService = Retrofit.Builder()
            .client(OkHttpClient.Builder()
                    .callTimeout(10, TimeUnit.SECONDS)
                    .build())
            .baseUrl(HOST_CHATBOT)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
            .create(PlaySoundsService::class.java)

    private val nuulsService = Retrofit.Builder()
            .client(OkHttpClient.Builder()
                    .callTimeout(10, TimeUnit.SECONDS)
                    .build())
            .baseUrl("http://unused/")
            .build()
            .create(NuulsService::class.java)

    suspend fun listRemoteSounds(): ServiceResponse<List<RemoteSoundBite>> {
        val response = playSoundsService.getHtml()
        val responseBody = response.body()
        if (!response.isSuccessful || responseBody == null) {
            return ServiceResponse.Error(response.code())
        }
        return withContext(Dispatchers.IO) {
            val html = String(responseBody.bytes())
            val blocks = html.split("<tr>")
                    .drop(2)

            ServiceResponse.Success(blocks.map { block ->
                val friendlyName = block.split("<td>")[1].split("</td>").first()
                val url = block.split("data-link=\"")[1].split("\"").first()
                val fileExtension = url.substringAfterLast('.', missingDelimiterValue = "")
                val fileName = "$friendlyName.$fileExtension"
                RemoteSoundBite(fileName, url)
            })
        }
    }

    suspend fun downloadRemoteSound(remoteSoundBite: RemoteSoundBite, destination: String) {
        val response = nuulsService.get(remoteSoundBite.url)
        val responseBody = response.body()
        if (!response.isSuccessful || responseBody == null) {
            throw RuntimeException("Unable to download $remoteSoundBite, response=$response")
        }
        withContext(Dispatchers.IO) {
            val input = responseBody.byteStream()
            val output = FileOutputStream("$destination/${remoteSoundBite.fileName}")
            input.transferTo(output)
            output.close()
            input.close()
        }
    }
}