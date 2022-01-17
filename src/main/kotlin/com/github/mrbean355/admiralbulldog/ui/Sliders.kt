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

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.RangeSlider
import androidx.compose.material.Slider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlin.math.roundToInt

@Composable
fun IntSlider(
    value: Int,
    onValueChange: (Int) -> Unit,
    valueRange: IntRange,
    modifier: Modifier = Modifier
) {
    Slider(
        value = value.toFloat(),
        onValueChange = {
            onValueChange(it.roundToInt())
        },
        valueRange = valueRange.first.toFloat()..valueRange.last.toFloat(),
        modifier = modifier
    )
}

@Composable
@OptIn(ExperimentalMaterialApi::class)
fun IntRangeSlider(
    values: IntRange,
    onValueChange: (IntRange) -> Unit,
    valueRange: IntRange,
    modifier: Modifier = Modifier
) {
    RangeSlider(
        values = values.first.toFloat()..values.last.toFloat(),
        onValueChange = {
            onValueChange(it.start.roundToInt()..it.endInclusive.roundToInt())
        },
        valueRange = valueRange.first.toFloat()..valueRange.last.toFloat(),
        modifier = modifier
    )
}