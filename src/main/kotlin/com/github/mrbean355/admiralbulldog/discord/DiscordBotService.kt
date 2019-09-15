package com.github.mrbean355.admiralbulldog.discord

import com.github.mrbean355.admiralbulldog.assets.SoundFile
import com.github.mrbean355.admiralbulldog.bytes.OnBountyRunesSpawn
import com.github.mrbean355.admiralbulldog.bytes.SoundByte
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query

fun shouldPlayOnDiscord(soundByte: SoundByte): Boolean {
    return ConfigPersistence.isUsingDiscordBot() && soundByte is OnBountyRunesSpawn
}

fun playSoundOnDiscord(soundFile: SoundFile, token: String = ConfigPersistence.getDiscordToken()) {
    val service = Retrofit.Builder()
            .baseUrl("http://roonsbot.co.za:26382")
            .build()
            .create(DiscordBotService::class.java)

    if (token.isBlank()) {
        println("Blank token set!")
        return
    }
    val fileName = soundFile.path.substringAfterLast('/')
    service.playSound(token, fileName).enqueue(object : Callback<ResponseBody> {

        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
            println("Response: ${response.code()}")
        }

        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
            println("Failure: $t")
        }
    })
}

private interface DiscordBotService {

    @GET("/")
    fun playSound(@Query("token") token: String, @Query("soundFileName") soundFileName: String): Call<ResponseBody>
}