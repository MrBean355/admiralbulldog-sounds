package com.github.mrbean355.admiralbulldog.arch

import okhttp3.ResponseBody
import retrofit2.Response

suspend fun <T> callService(block: suspend () -> Response<T>): Response<T> {
    return try {
        block()
    } catch (t: Throwable) {
        Response.error<T>(999, ResponseBody.create(null, ""))
    }
}