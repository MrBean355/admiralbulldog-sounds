package com.github.mrbean355.admiralbulldog.persistence.migration

import com.google.gson.JsonElement
import com.google.gson.JsonObject

@Suppress("NOTHING_TO_INLINE")
inline operator fun JsonObject.set(property: String, element: JsonElement) {
    add(property, element)
}

@Suppress("NOTHING_TO_INLINE")
inline operator fun JsonObject.set(property: String, element: String) {
    addProperty(property, element)
}

@Suppress("NOTHING_TO_INLINE")
inline operator fun JsonObject.set(property: String, element: Number) {
    addProperty(property, element)
}

@Suppress("NOTHING_TO_INLINE")
inline operator fun JsonObject.set(property: String, element: Boolean) {
    addProperty(property, element)
}

@Suppress("NOTHING_TO_INLINE")
inline operator fun JsonObject.set(property: String, element: Char) {
    addProperty(property, element)
}