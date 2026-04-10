package com.github.mrbean355.admiralbulldog.exception

import com.github.mrbean355.admiralbulldog.common.DISCORD_BUTTON
import com.github.mrbean355.admiralbulldog.common.URL_DISCORD_SERVER_INVITE
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.common.showError
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
class UncaughtExceptionHandlerImpl(private val hostServices: HostServices) : Thread.UncaughtExceptionHandler {

    override fun uncaughtException(t: Thread, e: Throwable) {
        if (e is BindException) {
            handleBindException()
        } else {
            handleGenericException(t, e)
        }
    }

    private fun handleBindException() {
        showDialog(getString("header_app_already_running"), getString("content_app_already_running"), exitAfterwards = true)
    }

    private fun handleGenericException(t: Thread, e: Throwable) {
        e.writeExceptionLog(CrashLogFile, thread = t)
        showDialog(getString("header_unknown_error"), getString("content_unexpected_exception").format(File(CrashLogFile.path).absolutePath))
    }

    private fun showDialog(header: String, content: String, exitAfterwards: Boolean = false) {
        runLater {
            showError(header, content, DISCORD_BUTTON, ButtonType.OK) {
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