package com.github.mrbean355.admiralbulldog.settings

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import com.github.mrbean355.admiralbulldog.ui.components.LabeledCheckbox
import com.github.mrbean355.admiralbulldog.ui.openComposeScreen

@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    Column(
        modifier = Modifier.padding(24.dp).width(500.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val appVolume by viewModel.appVolume.collectAsState()
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(getString("label_volume"), modifier = Modifier.weight(1f))
            Slider(
                value = appVolume.toFloat(),
                onValueChange = { viewModel.setAppVolume(it.toInt()) },
                valueRange = 0f..100f,
                modifier = Modifier.width(150.dp)
            )
            Text("${appVolume}%", modifier = Modifier.padding(start = 8.dp))
        }

        val darkMode by viewModel.darkMode.collectAsState()
        LabeledCheckbox(getString("label_dark_mode"), darkMode) { viewModel.setDarkMode(it) }

        if (viewModel.traySupported) {
            Text(getString("settings_header_system_tray"), style = MaterialTheme.typography.titleMedium)

            val minimizeToTray by viewModel.minimizeToTray.collectAsState()
            LabeledCheckbox(getString("label_minimise_to_tray"), minimizeToTray) { viewModel.setMinimizeToTray(it) }

            val alwaysShowTrayIcon by viewModel.alwaysShowTrayIcon.collectAsState()
            LabeledCheckbox(getString("label_always_show_tray_icon"), alwaysShowTrayIcon) { viewModel.setAlwaysShowTrayIcon(it) }
        }

        Text(getString("settings_header_updates"), style = MaterialTheme.typography.titleMedium)

        val appUpdateFreq by viewModel.appUpdateFrequency.collectAsState()
        UpdateRow(
            label = getString("settings_field_app_update"),
            currentFreq = appUpdateFreq,
            frequencies = viewModel.updateFrequencies,
            onFrequencySelected = { viewModel.setAppUpdateFrequency(it) },
            onCheckClicked = { viewModel.onCheckForAppUpdateClicked() }
        )

        val soundsUpdateFreq by viewModel.soundsUpdateFrequency.collectAsState()
        UpdateRow(
            label = getString("settings_field_sounds_update"),
            currentFreq = soundsUpdateFreq,
            frequencies = viewModel.updateFrequencies,
            onFrequencySelected = { viewModel.setSoundsUpdateFrequency(it) },
            onCheckClicked = { viewModel.onUpdateSoundsClicked() }
        )

        if (viewModel.modEnabled) {
            val modUpdateFreq by viewModel.modUpdateFrequency.collectAsState()
            UpdateRow(
                label = getString("settings_field_mod_updates"),
                currentFreq = modUpdateFreq,
                frequencies = viewModel.updateFrequencies,
                onFrequencySelected = { viewModel.setModUpdateFrequency(it) },
                onCheckClicked = { viewModel.onCheckForModUpdateClicked() }
            )
        }

        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
            TextButton(onClick = { viewModel.onMoreInformationClicked() }) {
                Text(getString("btn_more_information"))
            }
            TextButton(onClick = { viewModel.onSendFeedbackClicked() }) {
                Text(getString("btn_send_feedback"))
            }
        }
    }
}

@Composable
fun UpdateRow(
    label: String,
    currentFreq: UpdateFrequency,
    frequencies: List<UpdateFrequency>,
    onFrequencySelected: (UpdateFrequency) -> Unit,
    onCheckClicked: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(label, modifier = Modifier.weight(1f))

        var expanded by remember { mutableStateOf(false) }
        Box {
            OutlinedButton(onClick = { expanded = true }) {
                Text(UpdateFrequencyStringConverter().toString(currentFreq) ?: "")
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                frequencies.forEach { freq ->
                    DropdownMenuItem(
                        text = { Text(UpdateFrequencyStringConverter().toString(freq) ?: "") },
                        onClick = {
                            onFrequencySelected(freq)
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(8.dp))
        Button(onClick = onCheckClicked) {
            Text(getString("btn_check_now"))
        }
    }
}

@Preview
@Composable
private fun SettingsScreenPreview() {
    // Initialise persistence so the ViewModel constructor doesn't crash during preview rendering
    try {
        ConfigPersistence.initialise()
    } catch (e: Exception) {
        // Ignore initialization errors during preview
    }

    MaterialTheme {
        SettingsScreen(SettingsViewModel())
    }
}

fun openSettingsScreen() {
    openComposeScreen(
        title = getString("title_settings"),
        viewModelFactory = { SettingsViewModel() }
    ) { viewModel ->
        SettingsScreen(viewModel)
    }
}
