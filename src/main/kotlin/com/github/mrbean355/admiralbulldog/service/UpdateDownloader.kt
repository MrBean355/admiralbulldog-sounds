package com.github.mrbean355.admiralbulldog.service

import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleLongProperty
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import org.slf4j.LoggerFactory
import java.io.File

class UpdateDownloader {
    private val logger = LoggerFactory.getLogger(UpdateDownloader::class.java)

    val totalBytes = SimpleLongProperty()
    val progress = SimpleDoubleProperty()
    var onComplete = { _: String -> }

    /**
     * Downloads the JAR asset of the given [releaseInfo], placing it in the current working directory.
     */
    suspend fun download(releaseInfo: ReleaseInfo) {
        progress.value = 0.0
        val jarAssetInfo = releaseInfo.getJarAssetInfo() ?: return
        val response = GitHubService.instance.downloadLatestRelease(jarAssetInfo.downloadUrl)
        val body = response.body()
        if (!response.isSuccessful || body == null) {
            logger.error("New version download failed: $response")
            return
        }
        withContext(Main) {
            totalBytes.value = body.contentLength()
        }
        val totalBytes = this.totalBytes.value.toDouble()
        val file = File(jarAssetInfo.name)
        logger.info("Writing ${totalBytes.toBigDecimal().toPlainString()} bytes to ${file.absolutePath}")
        file.outputStream().use { output ->
            body.byteStream().use { input ->
                var bytesCopied = 0L
                val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                var bytes = input.read(buffer)
                while (bytes >= 0) {
                    yield()
                    output.write(buffer, 0, bytes)
                    bytesCopied += bytes
                    withContext(Main) {
                        progress.value = bytesCopied / totalBytes
                    }
                    bytes = input.read(buffer)
                }
                logger.info("Finished downloading new version: ${jarAssetInfo.name}")
                withContext(Main) {
                    onComplete(file.absolutePath)
                }
            }
        }
    }
}