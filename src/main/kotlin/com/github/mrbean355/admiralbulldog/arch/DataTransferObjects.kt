package com.github.mrbean355.admiralbulldog.arch

import com.github.mrbean355.admiralbulldog.DISTRIBUTION
import com.github.mrbean355.admiralbulldog.persistence.VPK_FILE_NAME
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
        val name: String,
        val description: String,
        val version: Int,
        val parts: List<DotaModPart>
)

data class DotaModPart(
        val name: String,
        val url: String,
        val vpkPath: String
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
private const val SHA_512_ASSET_NAME = "${VPK_FILE_NAME}.sha512"

/** @return [AssetInfo] for the app's main JAR file. */
fun ReleaseInfo.getAppAssetInfo(): AssetInfo? {
    return assets.firstOrNull {
        it.name.matches(JAR_FILE_PATTERN)
    }
}

/** @return [AssetInfo] for the mod's VPK file. */
fun ReleaseInfo.getModAssetInfo(): AssetInfo? {
    return assets.firstOrNull {
        it.name == VPK_FILE_NAME
    }
}

/** @return [AssetInfo] for the mod's checksum file. */
fun ReleaseInfo.getModChecksumAssetInfo(): AssetInfo? {
    return assets.firstOrNull {
        it.name == SHA_512_ASSET_NAME
    }
}
