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

import retrofit2.Response

class ServiceResponse<T> private constructor(val statusCode: Int, val body: T?) {

    fun isSuccessful() = statusCode in 200..299

    @Suppress("FunctionName")
    companion object {
        fun <T> Success(body: T?) = ServiceResponse(200, body)
        fun <T> Error(statusCode: Int) = ServiceResponse<T>(statusCode, null)
        fun <T> Exception() = ServiceResponse<T>(999, null)
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
