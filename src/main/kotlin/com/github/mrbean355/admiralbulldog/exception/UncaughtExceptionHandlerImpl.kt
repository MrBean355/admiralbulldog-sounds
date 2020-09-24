package com.github.mrbean355.admiralbulldog.exception

import com.github.mrbean355.admiralbulldog.common.DISCORD_BUTTON
import com.github.mrbean355.admiralbulldog.common.URL_DISCORD_SERVER_INVITE
import com.github.mrbean355.admiralbulldog.common.error
import com.github.mrbean355.admiralbulldog.common.getString
import javafx.application.HostServices
import javafx.scene.control.ButtonType
import tornadofx.runLater
import java.io.File
import java.net.BindException
import kotlin.system.exitProcess

/**
 * [java.lang.Thread.UncaughtExceptionHandler] which creates a log file containing the stack trace with some additional
 * info. Also shows an alert to the user, asking them to report the issue on Discord.
 */
class UncaughtExceptionHandlerImpl(private val hostServices: HostServices)
    : Thread.UncaughtExceptionHandler {

    override fun uncaughtException(t: Thread, e: Throwable) {
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
        """.trimIndent(), exitAfterwards = true)
    }

    private fun handleGenericException(t: Thread, e: Throwable) {
        e.writeExceptionLog(CrashLogFile, thread = t)

        showDialog(getString("title_unknown_error"), """
            Whoops! Something bad has happened, sorry!
            Please consider reporting this issue so it can be fixed.

            An error log file was created here:
            ${File(CrashLogFile.path).absolutePath}

            Please send this file through Discord so we can fix this problem.
        """.trimIndent())
    }

    private fun showDialog(header: String, message: String, exitAfterwards: Boolean = false) {
        runLater {
            error(header, message, DISCORD_BUTTON, ButtonType.OK) {
                if (it === DISCORD_BUTTON) {
                    hostServices.showDocument(URL_DISCORD_SERVER_INVITE)
                }
                if (exitAfterwards) {
                    exitProcess(-1)
                }
            }
        }
    }
}