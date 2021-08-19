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

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

/**
 * [OutlinedTextField] which allows the user to input a number between [min] and [max].
 * Appends a percentage sign (%) when focus is lost.
 */
@Composable
fun PercentTextField(
    value: UInt,
    min: UInt,
    max: UInt,
    label: String,
    onValueChange: (UInt) -> Unit,
    modifier: Modifier = Modifier
) {
    check(min <= max) { "Min ($min) cannot be greater than max ($max)" }

    val interactionSource = remember { MutableInteractionSource() }
    val focused by interactionSource.collectIsFocusedAsState()
    val bounded = if (focused) value else value.coerceIn(min..max)
    if (bounded != value) {
        onValueChange(bounded)
    }

    OutlinedTextField(
        value = bounded.format(focused),
        onValueChange = { text ->
            text.trim()
                .dropLastWhile { it == '%' }
                .ifEmpty { "0" }
                .toUIntOrNull()
                ?.let(onValueChange)
        },
        label = { Text(label) },
        modifier = modifier,
        interactionSource = interactionSource
    )
}

private fun UInt.format(focused: Boolean): String {
    return if (focused) {
        if (this > 0u) toString() else ""
    } else {
        "${this}%"
    }
}