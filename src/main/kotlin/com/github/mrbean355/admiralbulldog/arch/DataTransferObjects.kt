package com.github.mrbean355.admiralbulldog.arch

import com.github.mrbean355.admiralbulldog.ui.VPK_FILE_NAME
import com.google.gson.annotations.SerializedName
import java.util.Date

//==================== DTOs ====================\\

data class ReleaseInfo(
        @SerializedName("tag_name")
        val tagName: String,
        val name: String,
        @SerializedName("html_url")
        val htmlUrl: String,
        @SerializedName("published_at")
        val publishedAt: Date,
        val assets: List<AssetInfo>)

data class AssetInfo(
        val name: String,
        @SerializedName("browser_download_url")
        val downloadUrl: String)

//==================== Extensions ====================\\

private val JAR_FILE_PATTERN = Regex("admiralbulldog-sounds-.*\\.jar")
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
