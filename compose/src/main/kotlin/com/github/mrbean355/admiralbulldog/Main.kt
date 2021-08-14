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

package com.github.mrbean355.admiralbulldog

import androidx.compose.desktop.DesktopMaterialTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.application
import com.github.mrbean355.admiralbulldog.core.AppWindow
import com.github.mrbean355.admiralbulldog.gsi.GameStateIntegrationServer
import com.github.mrbean355.admiralbulldog.sounds.SoundsScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlin.system.exitProcess

val AppScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

private val Tabs = mutableListOf(
    stringResource("tab_home"),
    stringResource("tab_sounds"),
    stringResource("tab_discord_bot"),
    stringResource("tab_dota_mods"),
    stringResource("tab_settings")
)

fun main() = application {
    AppWindow(
        title = stringResource("title_application"),
        escapeClosesWindow = false,
        onCloseRequest = { exitProcess(0) }
    ) {
        DesktopMaterialTheme(darkColors()) {
            Column(Modifier.fillMaxSize()) {
                var selected by remember { mutableStateOf(0) }

                TabRow(selected, modifier = Modifier.height(48.dp)) {
                    Tabs.forEachIndexed { index, title ->
                        Tab(index == selected, onClick = { selected = index }) {
                            Text(title)
                        }
                    }
                }

                when (selected) {
                    0 -> HomeScreen()
                    1 -> SoundsScreen()
                    else -> PlaceholderScreen()
                }
            }
        }

        GameStateIntegrationServer.start()
    }
}

@Composable
fun PlaceholderScreen() {
    Text("TODO")
}