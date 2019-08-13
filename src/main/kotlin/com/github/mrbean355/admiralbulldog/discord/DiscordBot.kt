package com.github.mrbean355.admiralbulldog.discord

import com.github.mrbean355.admiralbulldog.assets.SoundFile
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query

fun playSoundOnDiscord(soundFile: SoundFile) {
    val service = Retrofit.Builder()
            .baseUrl("http://localhost:26382")
            .build()
            .create(DiscordBotService::class.java)

    service.playSound("t0k3n").enqueue(object : Callback<ResponseBody> {

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
    fun playSound(@Query("token") token: String): Call<ResponseBody>
}