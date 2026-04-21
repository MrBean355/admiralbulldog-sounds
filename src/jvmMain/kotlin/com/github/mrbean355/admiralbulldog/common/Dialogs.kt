package com.github.mrbean355.admiralbulldog.common

import com.github.mrbean355.admiralbulldog.ui.components.showComposeAlert

/**
 * Project-specific alert buttons to decouple logic from JavaFX.
 */
enum class AlertButton(val text: String) {
    OK(getString("btn_ok")),
    CANCEL(getString("btn_cancel")),
    FINISH(getString("btn_finish")),
    RETRY(getString("btn_retry")),
    WHATS_NEW(getString("btn_whats_new")),
    UPDATE(getString("btn_update")),
    NEXT(getString("btn_next")),
    DISCORD(getString("btn_join_discord")),
    MORE_INFO(getString("btn_more_info"))
}

enum class AlertType {
    INFORMATION,
    WARNING,
    ERROR
}

fun showInformation(header: String, content: String? = null, vararg buttons: AlertButton, actionFn: (AlertButton) -> Unit = {}) =
    showAlert(AlertType.INFORMATION, header, content, *buttons, actionFn = actionFn)

fun showWarning(header: String, content: String? = null, vararg buttons: AlertButton, actionFn: (AlertButton) -> Unit = {}) =
    showAlert(AlertType.WARNING, header, content, *buttons, actionFn = actionFn)

fun showError(header: String, content: String? = null, vararg buttons: AlertButton, actionFn: (AlertButton) -> Unit = {}) =
    showAlert(AlertType.ERROR, header, content, *buttons, actionFn = actionFn)

fun showAlert(
    type: AlertType,
    header: String,
    content: String? = null,
    vararg buttons: AlertButton,
    title: String? = null,
    actionFn: (AlertButton) -> Unit = {}
) {
    showComposeAlert(
        title = title ?: getString("title_app"),
        header = header,
        content = content,
        buttons = if (buttons.isEmpty()) listOf(AlertButton.OK) else buttons.toList(),
        icon = when (type) {
            AlertType.INFORMATION -> {
                { MonkaHmmIconPainter() }
            }

            AlertType.WARNING -> {
                { MonkaSIconPainter() }
            }

            AlertType.ERROR -> {
                { SadKekIconPainter() }
            }
        },
        actionFn = actionFn
    )
}