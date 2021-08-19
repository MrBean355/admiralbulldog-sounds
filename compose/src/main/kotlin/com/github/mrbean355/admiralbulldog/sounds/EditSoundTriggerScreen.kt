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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import com.github.mrbean355.admiralbulldog.core.AppWindow
import com.github.mrbean355.admiralbulldog.core.PercentTextField
import com.github.mrbean355.admiralbulldog.sounds.triggers.SoundTrigger
import com.github.mrbean355.admiralbulldog.stringResource

@Composable
fun EditSoundTriggerScreen(
    trigger: SoundTrigger,
    onClose: () -> Unit
) = AppWindow(
    trigger.name,
    onCloseRequest = onClose
) {
    // TODO: read from & write to settings file.
    var enabled by remember { mutableStateOf(true) }
    var chance by remember { mutableStateOf(50u) }
    var minRate by remember { mutableStateOf(75u) }
    var maxRate by remember { mutableStateOf(150u) }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text(trigger.description, modifier = Modifier)
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(stringResource("label_trigger_enabled"))
            Switch(enabled, onCheckedChange = { enabled = !enabled })
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            PercentTextField(chance, 0u, 100u, stringResource("label_trigger_chance"), { chance = it })
            IconButton({}) {
                Icon(rememberVectorPainter(Icons.Filled.Info), "")
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            PercentTextField(minRate, 25u, 300u, stringResource("label_trigger_rate_min"), { minRate = it }, modifier = Modifier.weight(1f))
            PercentTextField(maxRate, 25u, 300u, stringResource("label_trigger_rate_max"), { maxRate = it }, modifier = Modifier.weight(1f))
            IconButton({}) {
                Icon(rememberVectorPainter(Icons.Filled.Info), stringResource("cd_help_icon"))
            }
        }
    }
}