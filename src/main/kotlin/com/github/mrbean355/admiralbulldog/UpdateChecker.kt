package com.github.mrbean355.admiralbulldog

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.value.ObservableValue
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET

/**
 * Check if the `latest-version` file on GitHub has a newer version that the current one.
 */
fun checkForNewVersion(): ObservableValue<Boolean> {
    val result = SimpleBooleanProperty(false)
    GlobalScope.launch {
        val response = service().getVersionFile()
        if (response.isSuccessful) {
            val latestVersion = response.body().orEmpty().toInt()
            val currentVersion = GitHubService::class.java.classLoader.getResource("latest-version")?.readText().orEmpty().toInt()
            if (latestVersion > currentVersion) {
                result.set(true)
            }
        } else {
            println("Failed to download version file: ${response.code()}")
        }
    }
    return result
}

private fun service() = Retrofit.Builder()
        .baseUrl("https://raw.githubusercontent.com/MrBean355/admiralbulldog-sounds/master/src/main/resources/")
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()
        .create(GitHubService::class.java)

private interface GitHubService {
    @GET("latest-version")
    suspend fun getVersionFile(): Response<String>
}