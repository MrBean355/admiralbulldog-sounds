package com.github.mrbean355.admiralbulldog.ui

import kotlinx.coroutines.yield
import java.io.File
import java.io.InputStream
import java.util.ResourceBundle

private val strings: ResourceBundle = ResourceBundle.getBundle("strings")

fun getString(key: String): String {
    return if (strings.containsKey(key)) {
        strings.getString(key)
    } else {
        key
    }
}

fun getString(key: String, vararg formatArgs: Any?): String {
    return getString(key).format(*formatArgs)
}

fun String.replaceFileSeparators(): String {
    return replace("/", File.separator)
}

fun String.removeVersionPrefix(): String {
    return replace(Regex("^v"), "")
}

fun Double.format(decimalPlaces: Int): String {
    return "%.${decimalPlaces}f".format(this)
}

/**
 * Stream this [InputStream] into the given [file], calling [onProgress] with the current number of bytes written.
 */
suspend fun InputStream.streamToFile(file: File, onProgress: suspend (Long) -> Unit) {
    file.outputStream().use { output ->
        var bytesCopied = 0L
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        var bytes = read(buffer)
        while (bytes >= 0) {
            yield()
            output.write(buffer, 0, bytes)
            bytesCopied += bytes
            onProgress(bytesCopied)
            bytes = read(buffer)
        }
    }
}
