/*
 * Copyright 2023 Michael Johnston
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

package com.github.mrbean355.admiralbulldog.common

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

val RETRY_BUTTON = ButtonType(getString("btn_retry"), ButtonBar.ButtonData.OK_DONE)
val WHATS_NEW_BUTTON = ButtonType(getString("btn_whats_new"), ButtonBar.ButtonData.HELP_2)
val UPDATE_BUTTON = ButtonType(getString("btn_update"), ButtonBar.ButtonData.NEXT_FORWARD)
val DISCORD_BUTTON = ButtonType(getString("btn_join_discord"), ButtonBar.ButtonData.OK_DONE)
val MORE_INFO_BUTTON = ButtonType(getString("btn_more_info"), ButtonBar.ButtonData.HELP_2)

inline fun showInformation(header: String, content: String? = null, vararg buttons: ButtonType, actionFn: Alert.(ButtonType) -> Unit = {}) =
    showAlert(INFORMATION, header, content, *buttons, owner = FX.primaryStage, title = getString("title_app"), graphic = ImageView(MonkaHmmIcon()), actionFn = actionFn)

inline fun showWarning(header: String, content: String? = null, vararg buttons: ButtonType, actionFn: Alert.(ButtonType) -> Unit = {}) =
    showAlert(WARNING, header, content, *buttons, owner = FX.primaryStage, title = getString("title_app"), graphic = ImageView(MonkaSIcon()), actionFn = actionFn)

inline fun showError(header: String, content: String? = null, vararg buttons: ButtonType, actionFn: Alert.(ButtonType) -> Unit = {}) =
    showAlert(ERROR, header, content, *buttons, owner = FX.primaryStage, title = getString("title_app"), graphic = ImageView(SadKekIcon()), actionFn = actionFn)

inline fun showAlert(
    type: Alert.AlertType,
    header: String,
    content: String? = null,
    vararg buttons: ButtonType,
    owner: Window? = null,
    title: String? = null,
    graphic: Node? = null,
    actionFn: Alert.(ButtonType) -> Unit = {}
): Alert {
    val alert = Alert(type, content ?: "", *buttons)
    title?.let { alert.title = it }
    alert.headerText = header
    owner?.also { alert.initOwner(it) }
    graphic?.let { alert.graphic = it }
    val buttonClicked = alert.showAndWait()
    if (buttonClicked.isPresent) {
        alert.actionFn(buttonClicked.get())
    }
    return alert
}