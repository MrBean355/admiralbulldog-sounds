package com.github.mrbean355.admiralbulldog.service

import com.github.mrbean355.admiralbulldog.ui.VPK_FILE_NAME
import com.google.gson.annotations.SerializedName
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url
import java.util.Date

private const val SHA_512_ASSET_NAME = "${VPK_FILE_NAME}.sha512"

interface GitHubService {

    /** Query the project's GitHub releases for the latest one. */
    @GET("https://api.github.com/repos/MrBean355/admiralbulldog-sounds/releases/latest")
    suspend fun getLatestAppReleaseInfo(): Response<ReleaseInfo>

    /** Query the mod's GitHub releases for the latest one. */
    @GET("https://api.github.com/repos/MrBean355/admiralbulldog-mod/releases/latest")
    suspend fun getLatestModReleaseInfo(): Response<ReleaseInfo>

    /** Download the release at the given [url]. */
    @GET
    @Streaming
    suspend fun downloadLatestRelease(@Url url: String): Response<ResponseBody>

    companion object {
        val instance: GitHubService = Retrofit.Builder()
                .baseUrl("http://unused")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(GitHubService::class.java)
    }
}

data class ReleaseInfo(
        @SerializedName("tag_name") val tagName: String,
        val name: String,
        @SerializedName("html_url") val htmlUrl: String,
        @SerializedName("published_at") val publishedAt: Date,
        val assets: List<AssetInfo>) {

    fun getJarAssetInfo(): AssetInfo? {
        return assets.firstOrNull {
            it.name.matches(Regex("admiralbulldog-sounds-.*\\.jar"))
        }
    }

    fun getModAssetInfo(): AssetInfo? {
        return assets.firstOrNull {
            it.name == VPK_FILE_NAME
        }
    }

    fun getModChecksumAssetInfo(): AssetInfo? {
        return assets.firstOrNull {
            it.name == SHA_512_ASSET_NAME
        }
    }
}

data class AssetInfo(
        val name: String,
        @SerializedName("browser_download_url") val downloadUrl: String
)
