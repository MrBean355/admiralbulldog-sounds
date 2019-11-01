package com.github.mrbean355.admiralbulldog.persistence

import java.util.Date

object Notifications {
    private val items = mutableListOf<String>()

    fun put(message: String) {
        items += "[${Date()}] $message"
    }

    fun getAll(): List<String> {
        return items.toList()
    }

    fun clear() {
        items.clear()
    }
}