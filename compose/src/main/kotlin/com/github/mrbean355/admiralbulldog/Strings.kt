/*
 * Copyright 2021 Michael Johnston
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.mrbean355.admiralbulldog

import java.util.ResourceBundle

private val strings = ResourceBundle.getBundle("strings")

fun stringResource(key: String): String {
    return if (strings.containsKey(key)) {
        strings.getString(key)
    } else {
        println("[Warning] String resource not found: $key")
        key
    }
}

fun stringResource(key: String, vararg formatArgs: Any?): String {
    return stringResource(key).format(*formatArgs)
}

/** Format a float (e.g. 0.2) to a percentage string (e.g. 20%). */
fun Float.formatPercent(decimals: Int): String {
    return "%.${decimals}f%%".format(this * 100)
}