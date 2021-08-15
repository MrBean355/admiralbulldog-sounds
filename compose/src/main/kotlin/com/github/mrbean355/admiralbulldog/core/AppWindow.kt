/*
 * Copyright 2021 Michael Johnston
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.mrbean355.admiralbulldog.core

import androidx.compose.desktop.DesktopMaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowSize
import androidx.compose.ui.window.rememberWindowState

val DefaultWindowSize: WindowSize = WindowSize(600.dp, 400.dp)

@Composable
@OptIn(ExperimentalComposeUiApi::class)
fun AppWindow(
    title: String,
    size: WindowSize = DefaultWindowSize,
    escapeClosesWindow: Boolean = true,
    onCloseRequest: () -> Unit,
    content: @Composable FrameWindowScope.() -> Unit
) {
    Window(
        icon = painterResource("images/bulldog.jpg"),
        title = title,
        resizable = false,
        state = rememberWindowState(
            position = WindowPosition(Alignment.Center),
            size = size
        ),
        onCloseRequest = onCloseRequest,
        onPreviewKeyEvent = {
            if (escapeClosesWindow && it.key == Key.Escape && it.type == KeyEventType.KeyDown) {
                onCloseRequest()
                true
            } else {
                false
            }
        }
    ) {
        DesktopMaterialTheme(darkColors()) {
            Surface {
                content()
            }
        }
    }
}