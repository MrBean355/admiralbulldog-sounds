/*
 * Copyright 2022 Michael Johnston
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

data class PlaySoundsRequest(
    val userId: String,
    val token: String,
    val sounds: List<SoundToPlay>
)

data class SoundToPlay(
    val soundFileName: String,
    val volume: Int,
    val rate: Int
)

data class DotaMod(
    val key: String,
    val name: String,
    val description: String,
    val size: Int,
    val hash: String,
    val downloadUrl: String,
    val infoUrl: String
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

data class FeedbackRequest(
    val userId: String,
    val rating: Int,
    val comments: String
)

//==================== Extensions ====================\\

private val JAR_FILE_PATTERN = Regex("admiralbulldog-sounds-$DISTRIBUTION-.*\\.jar")

/** @return [AssetInfo] for the app's main JAR file. */
fun ReleaseInfo.getAppAssetInfo(): AssetInfo? {
    return assets.firstOrNull {
        it.name.matches(JAR_FILE_PATTERN)
    }
}
