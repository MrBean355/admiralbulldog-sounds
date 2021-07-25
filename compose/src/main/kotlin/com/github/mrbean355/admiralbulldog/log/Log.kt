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

package com.github.mrbean355.admiralbulldog.log

import org.slf4j.Logger
import org.slf4j.LoggerFactory

object Log {
    private val thisClass = Log::class.qualifiedName
    private val anonymousClass = """(\$\d+)+$""".toRegex()

    fun info(message: String, throwable: Throwable? = null) =
        getLogger().info(message, throwable)

    fun warn(message: String, throwable: Throwable? = null) =
        getLogger().warn(message, throwable)

    fun error(message: String, throwable: Throwable? = null) =
        getLogger().error(message, throwable)

    private fun getLogger(): Logger {
        val name = Throwable().stackTrace
            .first { it.className != thisClass }
            .className
            .substringAfterLast('.')
            .replace(anonymousClass, "")

        return LoggerFactory.getLogger(name)
    }
}