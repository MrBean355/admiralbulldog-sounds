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

package com.github.mrbean355.admiralbulldog.sounds

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerMoveFilter
import androidx.compose.ui.unit.dp

@Composable
fun SoundsScreen() {
    val viewModel = remember { SoundsViewModel() }
    val triggers by viewModel.triggers.collectAsState(emptyList())

    LazyColumn {
        items(triggers) {
            SoundTriggerItem(it.name, viewModel.description(it))
        }
    }
}

@Composable
fun SoundTriggerItem(name: String, description: String) {
    var highlighted by remember { mutableStateOf(false) }
    Column(
        Modifier.fillMaxWidth()
            .background(if (highlighted) MaterialTheme.colors.secondary else Color.Transparent)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .pointerMoveFilter(
                onEnter = {
                    highlighted = true
                    false
                },
                onExit = {
                    highlighted = false
                    false
                }
            )
    ) {
        Text(name, style = MaterialTheme.typography.body1)
        Text(description, style = MaterialTheme.typography.body2)
    }
}