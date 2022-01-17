/*
 * Copyright 2022 Michael Johnston
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

package com.github.mrbean355.admiralbulldog.sounds

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.Checkbox
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.triggers.SoundTriggerType
import com.github.mrbean355.admiralbulldog.ui.AppWindow

@Composable
fun ChooseSoundFilesScreen(triggerType: SoundTriggerType, onCloseRequest: () -> Unit) = AppWindow(
    title = triggerType.friendlyName,
    size = DpSize(400.dp, 800.dp),
    onCloseRequest = onCloseRequest
) {
    val viewModel = remember { ChooseSoundFilesViewModel(triggerType) }
    val items by viewModel.items.collectAsState(emptyList())
    val searchQuery by viewModel.searchQuery.collectAsState()
    val lazyListState = rememberLazyListState()

    Column {
        OutlinedTextField(
            value = searchQuery,
            label = { Text(text = getString("prompt_search")) },
            onValueChange = viewModel::onSearch,
            modifier = Modifier.padding(16.dp).fillMaxWidth()
        )
        Box {
            LazyColumn(
                state = lazyListState
            ) {
                items(items) { item ->
                    val isSelected by viewModel.isSelected(item).collectAsState(false)
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isSelected,
                            onCheckedChange = { viewModel.onSelectionChanged(item, it) }
                        )
                        Text(text = item.name)
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(onClick = { item.play() }) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = getString("content_desc_play_button")
                            )
                        }
                    }
                }
            }
            VerticalScrollbar(
                adapter = rememberScrollbarAdapter(lazyListState),
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }
    }
}