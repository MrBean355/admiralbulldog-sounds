/*
 * Copyright 2024 Michael Johnston
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

package com.github.mrbean355.admiralbulldog

import com.github.mrbean355.admiralbulldog.common.URL_JAVA_DOWNLOAD
import com.github.mrbean355.admiralbulldog.common.URL_LATEST_RELEASE
import com.github.mrbean355.admiralbulldog.common.getDistributionName
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.persistence.CONFIG_VERSION
import java.awt.Desktop
import java.net.URI
import javax.swing.JOptionPane.ERROR_MESSAGE
import javax.swing.JOptionPane.OK_CANCEL_OPTION
import javax.swing.JOptionPane.OK_OPTION
import javax.swing.JOptionPane.YES_NO_OPTION
import javax.swing.JOptionPane.YES_OPTION
import javax.swing.JOptionPane.showMessageDialog
import javax.swing.JOptionPane.showOptionDialog

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

/**
 * Check whether the `config.json` file was last saved by a more recent version of the app.
 * @return `true` if the app can safely open the config file.
 */
fun checkConfigVersion(fileVersion: Int): Boolean {
    if (fileVersion > CONFIG_VERSION) {
        val choice = showOptionDialog(null, getString("msg_app_unsupported"), getString("title_app"), YES_NO_OPTION, ERROR_MESSAGE, null, null, null)
        if (choice == YES_OPTION) {
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
