package com.github.mrbean355.admiralbulldog.arch

import com.github.mrbean355.admiralbulldog.service.AssetInfo
import com.github.mrbean355.admiralbulldog.service.ReleaseInfo
import kotlinx.coroutines.yield
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

class GitHubRepository {
    private val service = Retrofit.Builder()
            .baseUrl("http://unused")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GitHubService::class.java)

    suspend fun getLatestAppRelease(): Resource<ReleaseInfo> {
        return service.getLatestReleaseInfo(GitHubService.REPO_APP).toResource()
    }

    suspend fun getLatestModRelease(): Resource<ReleaseInfo> {
        return service.getLatestReleaseInfo(GitHubService.REPO_MOD).toResource()
    }

    suspend fun downloadAsset(assetInfo: AssetInfo, destination: String, onProgress: suspend (Progress) -> Unit): Resource<String> {
        val response = service.downloadLatestRelease(assetInfo.downloadUrl)
        val body = response.body()
        if (!response.isSuccessful || body == null) {
            return errorResource()
        }
        val directory = File(destination)
        if (!directory.exists()) {
            directory.mkdirs()
        }
        val file = File(directory, assetInfo.name)
        val totalBytes = body.contentLength()
        val totalBytesDouble = totalBytes.toDouble()
        body.streamToFile(file) {
            onProgress(Progress(it / totalBytesDouble, totalBytes))
        }
        return successResource(file.absolutePath)
    }

    class Progress(val current: Double, val total: Long)
}

private suspend fun ResponseBody.streamToFile(file: File, onProgress: suspend (Long) -> Unit) {
    file.outputStream().use { output ->
        byteStream().use { input ->
            var bytesCopied = 0L
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            var bytes = input.read(buffer)
            while (bytes >= 0) {
                yield()
                output.write(buffer, 0, bytes)
                bytesCopied += bytes
                onProgress(bytesCopied)
                bytes = input.read(buffer)
            }
        }
    }
}