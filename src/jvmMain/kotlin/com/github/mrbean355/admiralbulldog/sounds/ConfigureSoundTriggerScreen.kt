package com.github.mrbean355.admiralbulldog.sounds

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.github.mrbean355.admiralbulldog.assets.SoundBites
import com.github.mrbean355.admiralbulldog.common.MAX_BOUNTY_RUNE_TIMER
import com.github.mrbean355.admiralbulldog.common.MAX_CHANCE
import com.github.mrbean355.admiralbulldog.common.MAX_PERIOD
import com.github.mrbean355.admiralbulldog.common.MAX_RATE
import com.github.mrbean355.admiralbulldog.common.MIN_BOUNTY_RUNE_TIMER
import com.github.mrbean355.admiralbulldog.common.MIN_CHANCE
import com.github.mrbean355.admiralbulldog.common.MIN_PERIOD
import com.github.mrbean355.admiralbulldog.common.MIN_RATE
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import com.github.mrbean355.admiralbulldog.triggers.OnBountyRunesSpawn
import com.github.mrbean355.admiralbulldog.triggers.SoundTriggerType
import com.github.mrbean355.admiralbulldog.ui.components.LabeledCheckbox
import com.github.mrbean355.admiralbulldog.ui.components.NumericSpinner
import com.github.mrbean355.admiralbulldog.ui.openComposeScreen

@Composable
fun ConfigureSoundTriggerScreen(viewModel: ConfigureSoundTriggerViewModel) {
    val enabled by viewModel.enabled.collectAsState()
    val bountyRuneTimer by viewModel.bountyRuneTimer.collectAsState()
    val wisdomRuneTimer by viewModel.wisdomRuneTimer.collectAsState()
    val useSmartChance by viewModel.useSmartChance.collectAsState()
    val chance by viewModel.chance.collectAsState()
    val minPeriod by viewModel.minPeriod.collectAsState()
    val maxPeriod by viewModel.maxPeriod.collectAsState()
    val minRate by viewModel.minRate.collectAsState()
    val maxRate by viewModel.maxRate.collectAsState()
    val soundBiteCount by viewModel.soundBiteCount.collectAsState()

    var showSoundPicker by remember { mutableStateOf(false) }

    if (showSoundPicker) {
        SoundPickerDialog(
            type = viewModel.type,
            onDismiss = {
                showSoundPicker = false
                viewModel.refreshSoundBiteCount()
            }
        )
    }

    Column(
        modifier = Modifier
            .width(500.dp)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(viewModel.description, fontWeight = FontWeight.Bold)

        LabeledCheckbox(
            label = getString("label_enable_sound_trigger"),
            checked = enabled,
            onCheckedChange = { viewModel.setEnabled(it) }
        )

        if (viewModel.showBountyRuneTimer) {
            NumericSpinner(
                label = getString("label_rune_timer"),
                value = bountyRuneTimer,
                range = MIN_BOUNTY_RUNE_TIMER..MAX_BOUNTY_RUNE_TIMER,
                enabled = enabled,
                onValueChange = { viewModel.setBountyRuneTimer(it) }
            )
        }

        if (viewModel.showWisdomRuneTimer) {
            NumericSpinner(
                label = getString("label_rune_timer"),
                value = wisdomRuneTimer,
                range = MIN_BOUNTY_RUNE_TIMER..MAX_BOUNTY_RUNE_TIMER,
                enabled = enabled,
                onValueChange = { viewModel.setWisdomRuneTimer(it) }
            )
        }

        if (viewModel.showChance) {
            ElevatedCard {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(getString("header_chance_to_play"), style = MaterialTheme.typography.titleMedium)
                    if (viewModel.showSmartChance) {
                        LabeledCheckbox(
                            label = getString("label_use_smart_chance"),
                            checked = useSmartChance,
                            enabled = enabled,
                            onCheckedChange = { viewModel.setUseSmartChance(it) }
                        )
                    }
                    NumericSpinner(
                        label = getString("label_chance_to_play"),
                        value = chance,
                        range = MIN_CHANCE..MAX_CHANCE,
                        enabled = enabled && viewModel.enableChanceSpinner,
                        onValueChange = { viewModel.setChance(it) }
                    )
                }
            }
        }

        if (viewModel.showPeriod) {
            ElevatedCard {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(getString("header_periodic_trigger"), style = MaterialTheme.typography.titleMedium)
                    NumericSpinner(
                        label = getString("label_periodic_trigger_min"),
                        value = minPeriod,
                        range = MIN_PERIOD..MAX_PERIOD,
                        enabled = enabled,
                        onValueChange = { viewModel.setMinPeriod(it) }
                    )
                    NumericSpinner(
                        label = getString("label_periodic_trigger_max"),
                        value = maxPeriod,
                        range = MIN_PERIOD..MAX_PERIOD,
                        enabled = enabled,
                        onValueChange = { viewModel.setMaxPeriod(it) }
                    )
                }
            }
        }

        ElevatedCard {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(getString("header_playback_speed"), style = MaterialTheme.typography.titleMedium)
                NumericSpinner(
                    label = getString("label_min_playback_speed"),
                    value = minRate,
                    range = MIN_RATE..MAX_RATE,
                    enabled = enabled,
                    onValueChange = { viewModel.setMinRate(it) }
                )
                NumericSpinner(
                    label = getString("label_max_playback_speed"),
                    value = maxRate,
                    range = MIN_RATE..MAX_RATE,
                    enabled = enabled,
                    onValueChange = { viewModel.setMaxRate(it) }
                )
                Button(
                    onClick = { viewModel.onTestPlaybackSpeedClicked() },
                    enabled = enabled,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                ) {
                    Text(getString("btn_test_playback_speed"))
                }
            }
        }

        ElevatedCard {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(getString("header_sound_bite_selection"), style = MaterialTheme.typography.titleMedium)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(getString("label_sound_bite_count"), modifier = Modifier.weight(1f))
                    Text(soundBiteCount.toString(), style = MaterialTheme.typography.titleMedium)
                }
                Button(
                    onClick = { showSoundPicker = true },
                    enabled = enabled,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                ) {
                    Text(getString("btn_choose_sounds"))
                }
            }
        }
    }
}

@Composable
private fun SoundPickerDialog(type: SoundTriggerType, onDismiss: () -> Unit) {
    val allSounds = remember { SoundBites.getAll() }
    val initialSelection = remember { ConfigPersistence.getSoundsForType(type) }
    val currentSelection = remember { initialSelection.toMutableStateList() }
    var searchQuery by remember { mutableStateOf("") }

    val filteredSounds = remember(searchQuery) {
        if (searchQuery.isBlank()) allSounds
        else allSounds.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(type.friendlyName) },
        text = {
            Column(modifier = Modifier.width(400.dp).height(500.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text(getString("prompt_search")) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                val listState = rememberLazyListState()
                LazyColumn(state = listState, modifier = Modifier.weight(1f)) {
                    items(filteredSounds, key = { it.name }) { sound ->
                        val isSelected = currentSelection.contains(sound)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (isSelected) currentSelection.remove(sound)
                                    else currentSelection.add(sound)
                                }
                                .padding(vertical = 4.dp)
                        ) {
                            Checkbox(checked = isSelected, onCheckedChange = null)
                            Text(sound.name, modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                ConfigPersistence.saveSoundsForType(type, currentSelection.toList())
                onDismiss()
            }) {
                Text(getString("btn_save"))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(getString("btn_cancel"))
            }
        }
    )
}

@Preview
@Composable
private fun ConfigureSoundTriggerScreenPreview() {
    MaterialTheme {
        Surface {
            ConfigureSoundTriggerScreen(ConfigureSoundTriggerViewModel(OnBountyRunesSpawn::class))
        }
    }
}

fun openConfigureSoundTriggerScreen(type: SoundTriggerType, onClosed: () -> Unit) {
    openComposeScreen<ConfigureSoundTriggerViewModel>(
        title = type.friendlyName,
        viewModelFactory = { ConfigureSoundTriggerViewModel(type) },
        onCloseRequest = onClosed
    ) { viewModel: ConfigureSoundTriggerViewModel ->
        ConfigureSoundTriggerScreen(viewModel)
    }
}