package com.github.mrbean355.admiralbulldog

import com.github.mrbean355.admiralbulldog.arch.hostUrl
import com.github.mrbean355.admiralbulldog.ui.getString
import javafx.application.Application
import java.awt.Desktop
import java.net.URI
import javax.swing.JOptionPane.*

/** Program argument to point to a custom host. Points to prod if omitted. */
private const val ARG_HOST_URL = "--host-url"

/** Minimum Java major version supported by the app. */
private const val MIN_JAVA_VERSION = 8

fun main(args: Array<String>) {
    if (getJavaMajorVersion() >= MIN_JAVA_VERSION) {
        setCustomHostUrl(args)
        Application.launch(DotaApplication::class.java)
    } else {
        showJavaUnsupportedMessage()
    }
}

private fun getJavaMajorVersion(): Int {
    return System.getProperty("java.version").let { version ->
        if (version.startsWith("1.")) {
            version.substring(2, 3)
        } else {
            version.substringBefore('.')
        }.toIntOrNull() ?: -1
    }
}

private fun showJavaUnsupportedMessage() {
    val choice = showOptionDialog(null, getString("msg_old_java_version", getJavaMajorVersion()), getString("title_app"), YES_NO_OPTION, ERROR_MESSAGE, null, null, null)
    if (choice == YES_OPTION) {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(URI(URL_JAVA_DOWNLOAD))
        } else {
            showMessageDialog(null, getString("msg_visit_java_site", URL_JAVA_DOWNLOAD))
        }
    }
}

private fun setCustomHostUrl(args: Array<String>) {
    val hostUrlArg = args.firstOrNull { it.startsWith(ARG_HOST_URL) } ?: return
    hostUrl = hostUrlArg.substringAfterLast('=')
    if (hostUrl.isNotBlank()) {
        println("Using custom host URL: $hostUrl")
    }
}
