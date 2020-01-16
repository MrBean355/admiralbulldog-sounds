package com.github.mrbean355.admiralbulldog.arch

import retrofit2.Response

data class Resource<T>(
        val isSuccessful: Boolean,
        val data: T?
)

fun <T> errorResource(): Resource<T> {
    return Resource(isSuccessful = false, data = null)
}

fun <T> successResource(data: T?): Resource<T> {
    return Resource(isSuccessful = true, data = data)
}

fun <T> Response<T>.toResource(): Resource<T> {
    return Resource(isSuccessful, body())
}
