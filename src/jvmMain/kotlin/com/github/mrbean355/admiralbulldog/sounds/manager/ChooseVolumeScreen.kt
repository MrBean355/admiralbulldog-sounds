package com.github.mrbean355.admiralbulldog.sounds.manager

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.mrbean355.admiralbulldog.common.MAX_INDIVIDUAL_VOLUME
import com.github.mrbean355.admiralbulldog.common.PlayIconPainter
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.ui.openComposeScreen

@Composable
fun ChooseVolumeScreen(viewModel: ChooseVolumeViewModel) {
    val query by viewModel.query.collectAsState()
    val volume by viewModel.volume.collectAsState()
    val selectedSoundBite by viewModel.selectedSoundBite.collectAsState()

    Column(
        modifier = Modifier.padding(16.dp).width(350.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(getString("label_search_sound_bite"))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { viewModel.onQueryChanged(it) },
                modifier = Modifier.weight(1f),
                singleLine = true,
                placeholder = { Text(getString("prompt_search")) }
            )

            // Simple Volume Control
            OutlinedTextField(
                value = volume.toString(),
                onValueChange = { it.toIntOrNull()?.let { v -> viewModel.onVolumeChanged(v.coerceIn(0, MAX_INDIVIDUAL_VOLUME)) } },
                modifier = Modifier.width(80.dp),
                singleLine = true,
                label = { Text("%") }
            )

            IconButton(
                onClick = { viewModel.onPlayClicked() },
                enabled = selectedSoundBite != null
            ) {
                Icon(
                    painter = PlayIconPainter(),
                    contentDescription = getString("tooltip_play_locally")
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Button(
                onClick = { viewModel.onDoneClicked() },
                enabled = selectedSoundBite != null
            ) {
                Text(getString("btn_done"))
            }
        }
    }
}

fun openChooseVolumeScreen(initialName: String = "", onDone: () -> Unit = {}) {
    openComposeScreen(
        title = getString("title_choose_volume"),
        viewModelFactory = { ChooseVolumeViewModel(initialName) },
        onCloseRequest = onDone
    ) { viewModel ->
        ChooseVolumeScreen(viewModel)
    }
}