package com.github.mrbean355.admiralbulldog

import com.github.mrbean355.admiralbulldog.ui.Alert
import com.github.mrbean355.admiralbulldog.ui.toNullable
import javafx.application.HostServices
import javafx.application.Platform
import javafx.scene.control.Alert
import javafx.scene.control.ButtonBar
import javafx.scene.control.ButtonType
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.net.BindException

/**
 * [java.lang.Thread.UncaughtExceptionHandler] which creates a log file containing the stack trace with some additional
 * info. Also shows an [Alert] to the user, asking them to report the issue on Discord.
 */
class UncaughtExceptionHandlerImpl(private val hostServices: HostServices)
    : Thread.UncaughtExceptionHandler {

    override fun uncaughtException(t: Thread?, e: Throwable?) {
        val file = File("crash_log.txt")
        val stringWriter = StringWriter()
        e?.printStackTrace(PrintWriter(stringWriter))
        val stackTrace = stringWriter.toString()
        file.writeText("""
            |os.name      = ${System.getProperty("os.name")}
            |os.version   = ${System.getProperty("os.version")}
            |os.arch      = ${System.getProperty("os.arch")}
            |java.version = ${System.getProperty("java.version")}
            |thread info  = $t
            
            |$stackTrace
        """.trimMargin())

        Platform.runLater {
            val discordButton = ButtonType("Discord", ButtonBar.ButtonData.OK_DONE)
            val action = Alert(type = Alert.AlertType.ERROR,
                    header = HEADER_EXCEPTION,
                    content = getMessage(e, file),
                    buttons = arrayOf(discordButton, ButtonType.OK)
            ).showAndWait().toNullable()

            if (action == discordButton) {
                hostServices.showDocument(URL_DISCORD_SERVER_INVITE)
            }
        }
    }

    private fun getMessage(e: Throwable?, logFile: File): String {
        return if (e is BindException) {
            """
                It looks like the app may already be running. Only 1 instance can be running at a time.
                
                If it's not already running, an error log file was created here:
                ${logFile.absolutePath}
                
                Please send it to the community on Discord.
            """.trimIndent()
        } else {
            """
                Whoops! Something bad has happened, sorry!
                Please consider reporting this issue so it can be fixed.

                An error log file was created here:
                ${logFile.absolutePath}

                Please send it to the community on Discord.
            """.trimIndent()
        }
    }
}