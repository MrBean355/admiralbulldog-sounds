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

package com.github.mrbean355.admiralbulldog.sounds.download

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.mrbean355.admiralbulldog.core.AppWindow
import com.github.mrbean355.admiralbulldog.core.SmallWindowSize
import com.github.mrbean355.admiralbulldog.formatPercent
import com.github.mrbean355.admiralbulldog.stringResource

@Composable
fun DownloadSoundsScreen(onCloseRequest: () -> Unit) = AppWindow(
    title = stringResource("title_download_sounds"),
    size = SmallWindowSize,
    onCloseRequest = onCloseRequest
) {
    val scope = rememberCoroutineScope()
    val viewModel = remember { DownloadSoundsViewModel(scope) }
    val total by viewModel.total.collectAsState()
    val current by viewModel.current.collectAsState()
    val progress = current / total.toFloat()

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(16.dp).fillMaxSize()
    ) {
        Text(stringResource("label_downloading_sounds"), style = MaterialTheme.typography.body1)
        if (total > 0) {
            LinearProgressIndicator(progress, Modifier.fillMaxWidth())
            Row {
                Text(stringResource("label_download_progress", current, total))
                Spacer(Modifier.weight(1f))
                Text(progress.formatPercent(decimals = 1))
            }
        } else {
            LinearProgressIndicator(Modifier.fillMaxWidth())
        }
        if (progress >= 1f) {
            Button(
                onClick = onCloseRequest,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(stringResource("action_done"))
            }
        }
    }
}