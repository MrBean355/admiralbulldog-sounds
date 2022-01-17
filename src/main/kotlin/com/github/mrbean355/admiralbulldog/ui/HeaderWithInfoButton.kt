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

package com.github.mrbean355.admiralbulldog.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.github.mrbean355.admiralbulldog.common.getString

@Composable
fun ContentWithInfoButton(
    onInfoClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        content()
        Icon(
            imageVector = Icons.Filled.Info,
            contentDescription = getString("content_desc_more_info"),
            modifier = Modifier.clickable { onInfoClick() }
        )
    }
}

@Composable
fun HeaderWithInfoButton(
    text: String,
    onInfoClick: () -> Unit
) {
    ContentWithInfoButton(onInfoClick) {
        Text(
            text = text,
            fontWeight = FontWeight.Bold
        )
    }
}