package com.github.mrbean355.admiralbulldog.common

import com.github.mrbean355.admiralbulldog.ui.components.showComposeAlert
import javafx.application.Platform
import javafx.scene.Node
import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType.ERROR
import javafx.scene.control.Alert.AlertType.INFORMATION
import javafx.scene.control.Alert.AlertType.WARNING
import javafx.scene.control.ButtonBar
import javafx.scene.control.ButtonType
import javafx.scene.image.ImageView
import javafx.stage.Window
import tornadofx.FX
import javax.swing.SwingUtilities

val RETRY_BUTTON = ButtonType(getString("btn_retry"), ButtonBar.ButtonData.OK_DONE)
val WHATS_NEW_BUTTON = ButtonType(getString("btn_whats_new"), ButtonBar.ButtonData.HELP_2)
val UPDATE_BUTTON = ButtonType(getString("btn_update"), ButtonBar.ButtonData.NEXT_FORWARD)
val DISCORD_BUTTON = ButtonType(getString("btn_join_discord"), ButtonBar.ButtonData.OK_DONE)
val MORE_INFO_BUTTON = ButtonType(getString("btn_more_info"), ButtonBar.ButtonData.HELP_2)

fun showInformation(header: String, content: String? = null, vararg buttons: ButtonType, actionFn: (ButtonType) -> Unit = {}) =
    showAlert(INFORMATION, header, content, *buttons, title = getString("title_app"), actionFn = actionFn)

fun showWarning(header: String, content: String? = null, vararg buttons: ButtonType, actionFn: (ButtonType) -> Unit = {}) =
    showAlert(WARNING, header, content, *buttons, title = getString("title_app"), actionFn = actionFn)

fun showError(header: String, content: String? = null, vararg buttons: ButtonType, actionFn: (ButtonType) -> Unit = {}) =
    showAlert(ERROR, header, content, *buttons, title = getString("title_app"), actionFn = actionFn)

fun showAlert(
    type: Alert.AlertType,
    header: String,
    content: String? = null,
    vararg buttons: ButtonType,
    owner: Window? = null,
    title: String? = null,
    graphic: Node? = null,
    actionFn: (ButtonType) -> Unit = {}
) {
    if (SwingUtilities.isEventDispatchThread()) {
        showComposeAlert(
            owner = null, // owner lookup logic could be added later
            title = title ?: getString("title_app"),
            header = header,
            content = content,
            buttons = if (buttons.isEmpty()) listOf(ButtonType.OK) else buttons.toList(),
            icon = when (type) {
                INFORMATION -> {
                    { MonkaHmmIconPainter() }
                }

                WARNING -> {
                    { MonkaSIconPainter() }
                }

                ERROR -> {
                    { SadKekIconPainter() }
                }

                else -> {
                    { MonkaHmmIconPainter() }
                }
            },
            actionFn = actionFn
        )
    } else {
        val showAndBlock = {
            val alert = Alert(type, content ?: "", *buttons)
            title?.let { alert.title = it }
            alert.headerText = header
            (owner ?: FX.primaryStage)?.also { alert.initOwner(it) }
            graphic?.let { alert.graphic = it } ?: run {
                alert.graphic = ImageView(
                    when (type) {
                        INFORMATION -> MonkaHmmIcon()
                        WARNING -> MonkaSIcon()
                        ERROR -> SadKekIcon()
                        else -> MonkaHmmIcon()
                    }
                )
            }
            val buttonClicked = alert.showAndWait()
            if (buttonClicked.isPresent) {
                actionFn(buttonClicked.get())
            }
        }
        if (Platform.isFxApplicationThread()) {
            showAndBlock()
        } else {
            Platform.runLater(showAndBlock)
        }
    }
}