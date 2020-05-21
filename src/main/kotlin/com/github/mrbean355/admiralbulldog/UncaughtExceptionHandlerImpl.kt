package com.github.mrbean355.admiralbulldog

import com.github.mrbean355.admiralbulldog.common.DISCORD_BUTTON
import com.github.mrbean355.admiralbulldog.common.URL_DISCORD_SERVER_INVITE
import com.github.mrbean355.admiralbulldog.common.error
import com.github.mrbean355.admiralbulldog.ui.getString
import javafx.application.HostServices
import javafx.application.Platform
import javafx.scene.control.ButtonType
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.net.BindException
import kotlin.system.exitProcess

/**
 * [java.lang.Thread.UncaughtExceptionHandler] which creates a log file containing the stack trace with some additional
 * info. Also shows an alert to the user, asking them to report the issue on Discord.
 */
class UncaughtExceptionHandlerImpl(private val hostServices: HostServices)
    : Thread.UncaughtExceptionHandler {

    override fun uncaughtException(t: Thread?, e: Throwable?) {
        if (e is BindException) {
            handleBindException()
        } else {
            handleGenericException(t, e)
        }
    }

    private fun handleBindException() {
        showDialog("App already running", """
            It looks like the app is already running.
            Please make sure there is only 1 instance of the app running.
            
            If this is not the case, please report it through Discord.
        """.trimIndent(), exit = true)
    }

    private fun handleGenericException(t: Thread?, e: Throwable?) {
        val file = File("crash_log.txt")
        val stringWriter = StringWriter()
        e?.printStackTrace(PrintWriter(stringWriter))
        val stackTrace = stringWriter.toString()
        file.writeText("""
            |app version  = $APP_VERSION
            |os.name      = ${System.getProperty("os.name")}
            |os.version   = ${System.getProperty("os.version")}
            |os.arch      = ${System.getProperty("os.arch")}
            |java.version = ${System.getProperty("java.version")}
            |thread info  = $t
            
            |$stackTrace
        """.trimMargin())

        showDialog(getString("title_unknown_error"), """
            Whoops! Something bad has happened, sorry!
            Please consider reporting this issue so it can be fixed.

            An error log file was created here:
            ${file.absolutePath}

            Please send this file through Discord so we can fix this problem.
        """.trimIndent())
    }

    private fun showDialog(header: String, message: String, exit: Boolean = false) {
        Platform.runLater {
            error(header, message, DISCORD_BUTTON, ButtonType.OK) {
                if (it === DISCORD_BUTTON) {
                    hostServices.showDocument(URL_DISCORD_SERVER_INVITE)
                }
                exitProcess(-1)
            }
        }
    }
}