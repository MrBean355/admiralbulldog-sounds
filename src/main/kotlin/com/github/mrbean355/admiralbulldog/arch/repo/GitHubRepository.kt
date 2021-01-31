/*
 * Copyright 2021 Michael Johnston
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.mrbean355.admiralbulldog.arch.repo

import com.github.mrbean355.admiralbulldog.arch.AssetInfo
import com.github.mrbean355.admiralbulldog.arch.ReleaseInfo
import com.github.mrbean355.admiralbulldog.arch.ServiceResponse
import com.github.mrbean355.admiralbulldog.arch.service.GitHubService
import com.github.mrbean355.admiralbulldog.arch.toServiceResponse
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import org.slf4j.LoggerFactory

class GitHubRepository {
    private val logger = LoggerFactory.getLogger(GitHubRepository::class.java)

    suspend fun getLatestAppRelease(): ServiceResponse<ReleaseInfo> = withContext(IO) {
        try {
            GitHubService.INSTANCE.getLatestReleaseInfo().toServiceResponse()
        } catch (t: Throwable) {
            logger.error("Failed to get latest app release", t)
            ServiceResponse.Exception()
        }

    }

    suspend fun downloadAsset(assetInfo: AssetInfo): ServiceResponse<ResponseBody> = withContext(IO) {
        try {
            GitHubService.INSTANCE.downloadLatestRelease(assetInfo.downloadUrl).toServiceResponse()
        } catch (t: Throwable) {
            logger.error("Failed to download asset: $assetInfo", t)
            ServiceResponse.Exception()
        }
    }
}
