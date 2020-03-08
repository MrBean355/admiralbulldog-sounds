package com.github.mrbean355.admiralbulldog.arch

import okhttp3.ResponseBody
import retrofit2.Response
import java.io.File
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

suspend fun <T> callService(block: suspend () -> Response<T>): Response<T> {
    return try {
        block()
    } catch (t: Throwable) {
        Response.error(999, ResponseBody.create(null, ""))
    }
}

/** @return whether the SHA-512 hash of this [File] equals (case insensitive) the given [checksum]. */
fun File.verifyChecksum(checksum: String): Boolean {
    return try {
        val messageDigest = MessageDigest.getInstance("SHA-512")
        val result = messageDigest.digest(readBytes())
        val convertedResult = BigInteger(1, result)
        var hashText = convertedResult.toString(16)
        while (hashText.length < 32) {
            hashText = "0$hashText"
        }
        hashText.equals(checksum, ignoreCase = true)
    } catch (e: NoSuchAlgorithmException) {
        true
    }
}
