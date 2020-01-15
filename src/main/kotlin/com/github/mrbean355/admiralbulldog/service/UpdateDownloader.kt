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
    var onComplete: (fileUrl: String) -> Unit = {}

    /**
     * Downloads the given asset, placing it in the [destination] directory.
     */
    suspend fun download(assetInfo: AssetInfo, destination: String) {
        progress.value = 0.0
        val response = GitHubService.instance.downloadLatestRelease(assetInfo.downloadUrl)
        val body = response.body()
        if (!response.isSuccessful || body == null) {
            logger.error("File download failed: $response")
            return
        }
        withContext(Main) {
            totalBytes.value = body.contentLength()
        }
        val totalBytes = this.totalBytes.value.toDouble()
        val dir = File(destination)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val file = File(dir, assetInfo.name)
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
                logger.info("Finished downloading file: ${assetInfo.name}")
                withContext(Main) {
                    onComplete(file.absolutePath)
                }
            }
        }
    }
}