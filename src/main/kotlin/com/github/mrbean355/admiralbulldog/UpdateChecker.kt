package com.github.mrbean355.admiralbulldog

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET

/**
 * Check if the `latest-version` file on GitHub has a newer version that the current one.
 */
fun checkForNewVersion(onNewVersion: () -> Unit) {
    val service = Retrofit.Builder()
            .baseUrl("https://raw.githubusercontent.com/MrBean355/dota2-integration/master/src/main/resources/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
            .create(GitHubService::class.java)

    service.getVersionFile().enqueue(object : Callback<String> {

        override fun onResponse(call: Call<String>, response: Response<String>) {
            if (response.isSuccessful) {
                try {
                    val latestVersion = response.body().orEmpty().toInt()
                    val currentVersion = GitHubService::class.java.classLoader.getResource("latest-version")?.readText().orEmpty().toInt()
                    if (latestVersion > currentVersion) {
                        onNewVersion()
                    }
                } catch (t: Throwable) {
                    println("Failed to compare versions: $t")
                }
            } else {
                println("Failed to download version file: ${response.code()}")
            }
        }

        override fun onFailure(call: Call<String>, t: Throwable) {
            println("Failed to download version file: $t")
        }
    })
}

private interface GitHubService {
    @GET("latest-version")
    fun getVersionFile(): Call<String>
}