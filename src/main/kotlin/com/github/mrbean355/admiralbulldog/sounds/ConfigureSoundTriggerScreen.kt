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
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Checkbox
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.github.mrbean355.admiralbulldog.common.MAX_BOUNTY_RUNE_TIMER
import com.github.mrbean355.admiralbulldog.common.MAX_CHANCE
import com.github.mrbean355.admiralbulldog.common.MAX_PERIOD
import com.github.mrbean355.admiralbulldog.common.MAX_RATE
import com.github.mrbean355.admiralbulldog.common.MIN_BOUNTY_RUNE_TIMER
import com.github.mrbean355.admiralbulldog.common.MIN_CHANCE
import com.github.mrbean355.admiralbulldog.common.MIN_PERIOD
import com.github.mrbean355.admiralbulldog.common.MIN_RATE
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.triggers.SoundTriggerType
import com.github.mrbean355.admiralbulldog.ui.AppDialog
import com.github.mrbean355.admiralbulldog.ui.AppWindow
import com.github.mrbean355.admiralbulldog.ui.ContentWithInfoButton
import com.github.mrbean355.admiralbulldog.ui.HeaderWithInfoButton
import com.github.mrbean355.admiralbulldog.ui.IntRangeSlider
import com.github.mrbean355.admiralbulldog.ui.IntSlider

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ConfigureSoundTriggerScreen(triggerType: SoundTriggerType, onCloseRequest: () -> Unit) = AppWindow(
    title = triggerType.friendlyName,
    size = DpSize(500.dp, Dp.Unspecified),
    onCloseRequest = onCloseRequest
) {
    val viewModel = remember(triggerType) { ConfigureSoundTriggerViewModel(triggerType) }
    val enabled by viewModel.enabled.collectAsState()

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(16.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(enabled, onCheckedChange = viewModel::onEnabledChanged)
            Text(text = triggerType.description)
        }

        PlaybackChance(viewModel)
        BountyRuneTimer(viewModel)
        Period(viewModel)
        PlaybackRate(viewModel)
        SoundBites(viewModel)
    }
}

@Composable
private fun PlaybackChance(viewModel: ConfigureSoundTriggerViewModel) {
    if (viewModel.showChance) {
        val chance by viewModel.chance.collectAsState()
        val smartChanceEnabled by viewModel.smartChanceEnabled.collectAsState()
        val showSlider by viewModel.showChanceSlider.collectAsState(true)
        var showChanceInfo by remember { mutableStateOf(false) }
        var showSmartChanceInfo by remember { mutableStateOf(false) }

        HeaderWithInfoButton(
            text = getString("header_chance_to_play"),
            onInfoClick = { showChanceInfo = true }
        )
        if (viewModel.showSmartChance) {
            ContentWithInfoButton(onInfoClick = { showSmartChanceInfo = true }) {
                Checkbox(smartChanceEnabled, onCheckedChange = viewModel::onSmartChanceChanged)
                Text(text = getString("label_use_smart_chance"))
            }
        }
        if (showSlider) {
            Text(text = getString("label_chance_to_play", chance))
            IntSlider(
                value = chance,
                onValueChange = viewModel::onChanceChanged,
                valueRange = MIN_CHANCE..MAX_CHANCE
            )
        }
        if (showChanceInfo) {
            AppDialog(
                title = getString("header_about_chance"),
                message = getString("content_about_chance"),
                onCloseRequest = { showChanceInfo = false }
            )
        }
        if (showSmartChanceInfo) {
            AppDialog(
                title = getString("header_about_smart_chance"),
                message = getString("content_about_smart_chance"),
                onCloseRequest = { showSmartChanceInfo = false }
            )
        }
    }
}

@Composable
private fun BountyRuneTimer(viewModel: ConfigureSoundTriggerViewModel) {
    if (viewModel.showRuneTimer) {
        val runeTimer by viewModel.runeTimer.collectAsState()
        var showInfo by remember { mutableStateOf(false) }

        HeaderWithInfoButton(
            text = getString("header_bounty_rune_timer"),
            onInfoClick = { showInfo = true }
        )
        Text(text = getString("label_bounty_rune_timer", runeTimer))
        IntSlider(
            value = runeTimer,
            onValueChange = viewModel::onRuneTimerChanged,
            valueRange = MIN_BOUNTY_RUNE_TIMER..MAX_BOUNTY_RUNE_TIMER
        )
        if (showInfo) {
            AppDialog(
                title = getString("header_about_bounty_rune_timer"),
                message = getString("content_about_bounty_rune_timer"),
                onCloseRequest = { showInfo = false }
            )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterialApi::class)
private fun Period(viewModel: ConfigureSoundTriggerViewModel) {
    if (viewModel.showPeriod) {
        val values by viewModel.period.collectAsState()
        var showInfo by remember { mutableStateOf(false) }

        HeaderWithInfoButton(
            text = getString("header_periodic_trigger"),
            onInfoClick = { showInfo = true }
        )
        if (values.first == values.last) {
            Text(text = getString("label_periodic_trigger_constant", values.first))
        } else {
            Text(text = getString("label_periodic_trigger_variable", values.first, values.last))
        }
        IntRangeSlider(
            values = values,
            onValueChange = viewModel::onPeriodChanged,
            valueRange = MIN_PERIOD..MAX_PERIOD
        )
        if (showInfo) {
            AppDialog(
                title = getString("header_about_periodic_trigger"),
                message = getString("content_about_periodic_trigger"),
                onCloseRequest = { showInfo = false }
            )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterialApi::class)
private fun PlaybackRate(viewModel: ConfigureSoundTriggerViewModel) {
    val values by viewModel.rate.collectAsState()
    var showInfo by remember { mutableStateOf(false) }

    HeaderWithInfoButton(
        text = getString("header_playback_speed"),
        onInfoClick = { showInfo = true }
    )
    if (values.first == values.last) {
        Text(text = getString("label_playback_speed_constant", values.first))
    } else {
        Text(text = getString("label_playback_speed_variable", values.first, values.last))
    }
    IntRangeSlider(
        values = values,
        valueRange = MIN_RATE..MAX_RATE,
        onValueChange = viewModel::onRateChanged
    )
    if (showInfo) {
        AppDialog(
            title = getString("header_about_playback_speed"),
            message = getString("content_about_playback_speed"),
            onCloseRequest = { showInfo = false }
        )
    }
}

@Composable
private fun SoundBites(viewModel: ConfigureSoundTriggerViewModel) {
    val soundBites by viewModel.soundBites.collectAsState()
    var showInfo by remember { mutableStateOf(false) }

    HeaderWithInfoButton(
        text = getString("header_sound_bite_selection"),
        onInfoClick = { showInfo = true }
    )
    val description = when (soundBites.size) {
        0 -> getString("label_sound_bite_selection_zero")
        1 -> getString("label_sound_bite_selection_one")
        else -> getString("label_sound_bite_selection_many", soundBites.size)
    }
    Text(text = description, maxLines = 2, overflow = TextOverflow.Ellipsis)
    OutlinedButton(onClick = { /* TODO */ }) {
        Text(text = getString("btn_choose_sound_bites"))
    }
    if (showInfo) {
        AppDialog(
            title = getString("header_about_sound_bites"),
            message = getString("content_about_sound_bites"),
            onCloseRequest = { showInfo = false }
        )
    }
}