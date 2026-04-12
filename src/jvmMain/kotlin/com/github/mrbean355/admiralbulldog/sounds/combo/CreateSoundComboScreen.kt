package com.github.mrbean355.admiralbulldog.sounds.combo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.mrbean355.admiralbulldog.assets.ComboSoundBite
import com.github.mrbean355.admiralbulldog.common.DeleteIconPainter
import com.github.mrbean355.admiralbulldog.common.PlayIconPainter
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.ui.openComposeScreen

@Composable
fun CreateSoundComboScreen(viewModel: CreateSoundComboViewModel) {
    val name by viewModel.name.collectAsState()
    val query by viewModel.query.collectAsState()
    val selectedSoundBite by viewModel.selectedSoundBite.collectAsState()
    val items by viewModel.items.collectAsState()

    Column(
        modifier = Modifier.padding(16.dp).width(450.dp).height(600.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = { viewModel.onNameChanged(it) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(getString("label_combo_name")) },
            singleLine = true
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { viewModel.onQueryChanged(it) },
                modifier = Modifier.weight(1f),
                label = { Text(getString("label_search_sound_bite")) },
                singleLine = true
            )
            Button(
                onClick = { viewModel.onAddClicked() },
                enabled = selectedSoundBite != null
            ) {
                Text(getString("btn_add_sound_bite"))
            }
        }

        Text(
            text = getString("label_combo_sounds", items.size),
            style = MaterialTheme.typography.titleMedium
        )

        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            itemsIndexed(items) { index, sound ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = sound.name, modifier = Modifier.weight(1f))
                    IconButton(onClick = { viewModel.onRemoveClicked(index) }) {
                        Icon(DeleteIconPainter(), contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.End)
        ) {
            IconButton(onClick = { viewModel.onPlayClicked() }, enabled = items.isNotEmpty()) {
                Icon(PlayIconPainter(), contentDescription = null)
            }
            Button(
                onClick = { viewModel.onSaveClicked() },
                enabled = name.isNotBlank() && items.size >= 2
            ) {
                Text(getString("btn_save"))
            }
        }
    }
}

fun openCreateSoundComboScreen(originalSound: ComboSoundBite? = null, onDone: () -> Unit = {}) {
    openComposeScreen(
        title = getString("title_create_sound_combo"),
        viewModelFactory = { CreateSoundComboViewModel(originalSound) },
        onCloseRequest = onDone
    ) { viewModel ->
        CreateSoundComboScreen(viewModel)
    }
}