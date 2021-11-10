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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.triggers.SOUND_TRIGGER_TYPES
import com.github.mrbean355.admiralbulldog.ui.AppWindow

@Composable
@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterialApi::class)
fun ViewSoundTriggersScreen(onCloseRequest: () -> Unit) = AppWindow(
    title = getString("title_toggle_sound_triggers"),
    size = DpSize(350.dp, Dp.Unspecified),
    onCloseRequest = onCloseRequest
) {
    val viewModel = remember { ViewSoundTriggersViewModel() }
    val enabledTextColour = MaterialTheme.colors.onSurface
    val disabledTextColour = enabledTextColour.copy(alpha = 0.5f)

    Column {
        SOUND_TRIGGER_TYPES.forEach { triggerType ->
            val textColour = if (viewModel.isEnabled(triggerType)) enabledTextColour else disabledTextColour

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { /* TODO */ }
                    .padding(vertical = 8.dp, horizontal = 16.dp)
            ) {
                Text(
                    text = triggerType.friendlyName,
                    color = textColour,
                    modifier = Modifier.align(Alignment.CenterStart)
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = getString("content_chevron"),
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }
        }
        Box(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 16.dp)
        ) {
            Button(
                onClick = { /* TODO */ }
            ) {
                Text(text = getString("btn_manage_sounds"))
            }
            OutlinedButton(
                onClick = { /* TODO */ },
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Text(text = getString("btn_help"))
            }
        }
    }
}