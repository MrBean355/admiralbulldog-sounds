/*
 * Copyright 2023 Michael Johnston
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

package com.github.mrbean355.admiralbulldog.exception

import com.github.mrbean355.admiralbulldog.APP_VERSION
import com.github.mrbean355.admiralbulldog.DISTRIBUTION
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.util.Date

@JvmInline
value class LogFile(val path: String)

val CrashLogFile = LogFile("crash_log.txt")
val PlaySoundErrorFile = LogFile("play_sound_error.txt")

fun Throwable.writeExceptionLog(
    file: LogFile,
    message: String = "",
    thread: Thread = Thread.currentThread()
) {
    val stackTrace = StringWriter().let {
        printStackTrace(PrintWriter(it))
        it.toString()
    }
    File(file.path).writeText(
        """
        |Unhandled exception on ${Date()}: $message
        |app version  = $APP_VERSION [$DISTRIBUTION]
        |os.name      = ${System.getProperty("os.name")}
        |os.version   = ${System.getProperty("os.version")}
        |os.arch      = ${System.getProperty("os.arch")}
        |java.version = ${System.getProperty("java.version")}
        |thread info  = $thread

        |$stackTrace
        """.trimMargin()
    )
}
