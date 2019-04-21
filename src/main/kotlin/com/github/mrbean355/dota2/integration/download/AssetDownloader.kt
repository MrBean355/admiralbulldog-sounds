package com.github.mrbean355.dota2.integration.download

import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.io.File
import java.io.FileOutputStream

/** Download all sounds for some groups. */
class AssetDownloader(private vararg val groups: String) {
    private val playSoundService = Retrofit.Builder()
            .baseUrl(HOST_PLAY_SOUNDS)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PlaySoundService::class.java)
    private val nuulsService = Retrofit.Builder()
            .baseUrl(HOST_NUULS)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NuulsService::class.java)

    fun run() {
        groups.forEach(this::downloadSounds)
    }

    private fun downloadSounds(group: String) {
        val response = playSoundService.getSounds(group).execute()
        if (!response.isSuccessful) {
            throw RuntimeException("Failed to get '$group' sounds list: ${response.code()}")
        }
        val body = response.body()?.get(MEMBER_NAME)?.asJsonObject ?: throw RuntimeException("Invalid '$group' data")
        val dir = "$DEST_DIR/$group"
        File(dir).mkdirs()
        body.entrySet().forEach {
            downloadSound(it.key, it.value.asString, dir)
        }
    }

    private fun downloadSound(name: String, url: String, folder: String) {
        val fileName = "$folder/$name.$FILE_TYPE"
        if (File(fileName).exists()) {
            return
        }
        val response = nuulsService.get(url.trim().removePrefix(HOST_NUULS)).execute()
        if (!response.isSuccessful) {
            throw RuntimeException("Failed to get sound '$url': ${response.code()}")
        }
        val stream = response.body()?.byteStream() ?: throw RuntimeException("Null body stream")
        val output = FileOutputStream(fileName)
        val buffer = ByteArray(4096)
        while (true) {
            val read = stream.read(buffer)
            if (read == -1) {
                break
            }
            output.write(buffer, 0, read)
        }
        output.close()
        stream.close()
        println("Downloaded: $name.$FILE_TYPE")
    }

    companion object {
        private const val HOST_PLAY_SOUNDS = "http://178.128.205.181:7379/"
        private const val HOST_NUULS = "https://i.nuuls.com/"
        private const val MEMBER_NAME = "HGETALL"
        const val DEST_DIR = "src/main/resources/sounds"
        const val FILE_TYPE = "mp3"
    }

    private interface PlaySoundService {

        @GET("HGETALL/playsounds:{group}")
        fun getSounds(@Path("group") group: String): Call<JsonObject>
    }

    private interface NuulsService {

        @GET("{name}")
        fun get(@Path("name") name: String): Call<ResponseBody>
    }
}