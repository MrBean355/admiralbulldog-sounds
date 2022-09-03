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

package com.github.mrbean355.admiralbulldog.common

import com.github.mrbean355.admiralbulldog.DISTRIBUTION
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import tornadofx.ViewModel
import java.io.File
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

/** Get a logger whose name is the simple name of the receiver class. */
val ViewModel.logger: Logger
    get() = LoggerFactory.getLogger(this::class.java.simpleName)

fun String.replaceFileSeparators(): String {
    return replace("/", File.separator)
}

fun String.removeVersionPrefix(): String {
    return replace(Regex("^v"), "")
}

fun getDistributionName(): String {
    return DISTRIBUTION.replaceFirstChar(Char::uppercaseChar).let {
        if (it == "Win") "Windows" else it
    }
}
