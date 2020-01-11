package com.github.mrbean355.admiralbulldog.service

import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url
import java.util.Date
import java.util.concurrent.TimeUnit

interface GitHubService {

    /** Query the project's GitHub releases for the latest one. */
    @GET("https://api.github.com/repos/MrBean355/admiralbulldog-sounds/releases/latest")
    suspend fun getLatestReleaseInfo(): Response<ReleaseInfo>

    /** Download the release at the given [url]. */
    @GET
    @Streaming
    suspend fun downloadLatestRelease(@Url url: String): Response<ResponseBody>

    companion object {
        val instance: GitHubService = Retrofit.Builder()
                .client(OkHttpClient.Builder()
                        .callTimeout(10, TimeUnit.SECONDS)
                        .build())
                .baseUrl("http://unused")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(GitHubService::class.java)
    }
}

data class ReleaseInfo(
        @SerializedName("tag_name") val tagName: String,
        val name: String,
        val draft: Boolean,
        @SerializedName("prerelease") val preRelease: Boolean,
        @SerializedName("html_url") val htmlUrl: String,
        @SerializedName("published_at") val publishedAt: Date,
        val assets: List<AssetInfo>) {

    fun getJarAssetInfo(): AssetInfo? {
        return assets.firstOrNull {
            it.name.matches(Regex("admiralbulldog-sounds-.*\\.jar"))
        }
    }
}

data class AssetInfo(
        val name: String,
        @SerializedName("browser_download_url") val downloadUrl: String
)
