package com.github.mrbean355.admiralbulldog.arch

import retrofit2.Response

class ServiceResponse<T> private constructor(val statusCode: Int, val body: T?) {

    fun isSuccessful() = statusCode in 200..299

    @Suppress("FunctionName")
    companion object {
        fun <T> Success(body: T?) = ServiceResponse(200, body)
        fun <T> Error(statusCode: Int) = ServiceResponse<T>(statusCode, null)
    }
}

fun <T> Response<T>.toServiceResponse(): ServiceResponse<T> {
    return toServiceResponse { it }
}

fun <T, R> Response<T>.toServiceResponse(transform: (T) -> R): ServiceResponse<R> {
    val body = body()
    return if (isSuccessful && body != null) {
        ServiceResponse.Success(transform(body))
    } else {
        ServiceResponse.Error(code())
    }
}

/** A sound on the PlaySounds page. */
data class RemoteSoundBite(
        /** Remote file name with extension. */
        val fileName: String,
        /** URL where the file is hosted. */
        val url: String)