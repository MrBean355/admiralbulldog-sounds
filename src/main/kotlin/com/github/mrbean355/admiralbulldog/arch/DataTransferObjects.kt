package com.github.mrbean355.admiralbulldog.arch

import com.github.mrbean355.admiralbulldog.DISTRIBUTION
import com.google.gson.annotations.SerializedName
import java.util.Date

//==================== DTOs ====================\\

data class CreateIdResponse(
        val userId: String
)

data class AnalyticsRequest(
        val userId: String,
        val properties: Map<String, String>
)

data class PlaySoundRequest(
        val userId: String,
        val token: String,
        val soundFileName: String,
        val volume: Int,
        val rate: Int
)

data class DotaMod(
        val id: String,
        val name: String,
        val description: String,
        val size: Long,
        val hash: String,
        val downloadUrl: String
)

data class ReleaseInfo(
        @SerializedName("tag_name")
        val tagName: String,
        val name: String,
        @SerializedName("html_url")
        val htmlUrl: String,
        @SerializedName("published_at")
        val publishedAt: Date,
        val assets: List<AssetInfo>
)

data class AssetInfo(
        val name: String,
        @SerializedName("browser_download_url")
        val downloadUrl: String
)

//==================== Extensions ====================\\

private val JAR_FILE_PATTERN = Regex("admiralbulldog-sounds-$DISTRIBUTION-.*\\.jar")

/** @return [AssetInfo] for the app's main JAR file. */
fun ReleaseInfo.getAppAssetInfo(): AssetInfo? {
    return assets.firstOrNull {
        it.name.matches(JAR_FILE_PATTERN)
    }
}
