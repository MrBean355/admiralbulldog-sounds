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

package com.github.mrbean355.admiralbulldog.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.ui.Hyperlink

@Composable
fun MainScreen() {
    val scope = rememberCoroutineScope()
    val viewModel = remember { MainViewModel(scope) }

    val heading by viewModel.heading.collectAsState("")
    val isLoading by viewModel.progressBarVisible.collectAsState(true)
    val infoMessage by viewModel.infoMessage.collectAsState("")

    Box {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(16.dp).align(Alignment.Center)
        ) {
            Text(
                text = heading,
                fontSize = 22.sp
            )
            if (isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            Text(
                text = infoMessage,
                fontSize = 14.sp,
                fontStyle = FontStyle.Italic
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(onClick = { viewModel.onChangeSoundsClicked() }) {
                    Text(text = getString("btn_change_sounds"))
                }
                Button(onClick = { viewModel.onDiscordBotClicked() }) {
                    Text(text = getString("btn_discord_bot"))
                }
                Button(onClick = { viewModel.onDotaModClicked() }) {
                    Text(text = getString("btn_dota_mods"))
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Hyperlink(
                    text = viewModel.version,
                    onClick = { viewModel.onVersionClicked() }
                )
                Text(text = "|")
                Hyperlink(
                    text = getString("lbl_app_settings"),
                    onClick = { viewModel.onSettingsClicked() }
                )
            }
        }
    }
}