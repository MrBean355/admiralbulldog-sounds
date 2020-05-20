package com.github.mrbean355.admiralbulldog

import com.github.mrbean355.admiralbulldog.ui.getString
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import tornadofx.FX
import tornadofx.ViewModel

/** Get a logger whose name is the simple name of the receiver class. */
val ViewModel.logger: Logger
    get() = LoggerFactory.getLogger(this::class.java.simpleName)

inline fun confirmation(header: String, content: String? = null, vararg buttons: ButtonType, actionFn: Alert.(ButtonType) -> Unit = {}) =
        tornadofx.confirmation(header, content, *buttons, owner = FX.primaryStage, title = getString("title_app"), actionFn = actionFn)

inline fun information(header: String, content: String? = null, vararg buttons: ButtonType, actionFn: Alert.(ButtonType) -> Unit = {}) =
        tornadofx.information(header, content, *buttons, owner = FX.primaryStage, title = getString("title_app"), actionFn = actionFn)

inline fun warning(header: String, content: String? = null, vararg buttons: ButtonType, actionFn: Alert.(ButtonType) -> Unit = {}) =
        tornadofx.warning(header, content, *buttons, owner = FX.primaryStage, title = getString("title_app"), actionFn = actionFn)

inline fun error(header: String, content: String? = null, vararg buttons: ButtonType, actionFn: Alert.(ButtonType) -> Unit = {}) =
        tornadofx.error(header, content, *buttons, owner = FX.primaryStage, title = getString("title_app"), actionFn = actionFn)
