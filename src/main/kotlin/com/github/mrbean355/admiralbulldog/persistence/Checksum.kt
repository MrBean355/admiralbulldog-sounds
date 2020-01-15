package com.github.mrbean355.admiralbulldog.persistence

import org.slf4j.LoggerFactory
import java.io.File
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

object Checksum {

    /** @return whether the SHA-512 hash of [file] matches the given [toMatch] string. */
    fun verify(file: File, toMatch: String): Boolean {
        return try {
            val messageDigest = MessageDigest.getInstance("SHA-512")
            val result = messageDigest.digest(file.readBytes())
            val convertedResult = BigInteger(1, result)
            var hashText = convertedResult.toString(16)
            while (hashText.length < 32) {
                hashText = "0$hashText"
            }
            hashText.equals(toMatch, ignoreCase = true)
        } catch (e: NoSuchAlgorithmException) {
            LoggerFactory.getLogger(Checksum::class.java).error("Unable to hash file", e)
            true
        }
    }
}