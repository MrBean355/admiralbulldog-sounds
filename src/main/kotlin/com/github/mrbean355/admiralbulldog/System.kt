package com.github.mrbean355.admiralbulldog

import com.github.mrbean355.admiralbulldog.common.URL_JAVA_DOWNLOAD
import com.github.mrbean355.admiralbulldog.common.URL_LATEST_RELEASE
import com.github.mrbean355.admiralbulldog.common.getDistributionName
import com.github.mrbean355.admiralbulldog.common.getString
import java.awt.Desktop
import java.net.URI
import javax.swing.JOptionPane.*

/** Minimum Java major version supported by the app. */
private const val MIN_JAVA_VERSION = 8

/**
 * Check whether the Java runtime is at least [MIN_JAVA_VERSION].
 * Shows a dialog with a link to download Java 8 if this is not the case.
 */
fun checkJavaVersion(): Boolean {
    if (getJavaMajorVersion() < MIN_JAVA_VERSION) {
        val choice = showOptionDialog(null, getString("msg_old_java_version", getJavaMajorVersion()), getString("title_app"), YES_NO_OPTION, ERROR_MESSAGE, null, null, null)
        if (choice == YES_OPTION) {
            tryBrowseUrl(URL_JAVA_DOWNLOAD)
        }
        return false
    }
    return true
}

/**
 * Check whether this distribution was built for the current operating system.
 * Shows a dialog with a link to download the latest app release if this is not the case.
 */
fun checkOperatingSystem(): Boolean {
    val osName = System.getProperty("os.name")
    val expected = when {
        osName.startsWith("Windows") -> "win"
        osName.startsWith("Mac") -> "mac"
        else -> "linux"
    }
    if (DISTRIBUTION != expected) {
        val choice = showOptionDialog(null, getString("msg_wrong_distribution", getDistributionName(), osName), getString("title_app"), OK_CANCEL_OPTION, ERROR_MESSAGE, null, null, null)
        if (choice == OK_OPTION) {
            tryBrowseUrl(URL_LATEST_RELEASE)
        }
        return false
    }
    return true
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

private fun tryBrowseUrl(url: String) {
    if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
        Desktop.getDesktop().browse(URI(url))
    } else {
        showMessageDialog(null, getString("msg_visit_site_manually", url))
    }
}
