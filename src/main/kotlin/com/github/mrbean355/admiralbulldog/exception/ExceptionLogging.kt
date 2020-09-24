package com.github.mrbean355.admiralbulldog.exception

import com.github.mrbean355.admiralbulldog.APP_VERSION
import com.github.mrbean355.admiralbulldog.DISTRIBUTION
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.util.Date

@Suppress("EXPERIMENTAL_FEATURE_WARNING")
inline class LogFile(val path: String)

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
    File(file.path).writeText("""
        |Unhandled exception on ${Date()}: $message
        |app version  = $APP_VERSION [$DISTRIBUTION]
        |os.name      = ${System.getProperty("os.name")}
        |os.version   = ${System.getProperty("os.version")}
        |os.arch      = ${System.getProperty("os.arch")}
        |java.version = ${System.getProperty("java.version")}
        |thread info  = $thread

        |$stackTrace
    """.trimMargin())
}
