package com.github.mrbean355.admiralbulldog.sounds.sync

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.github.mrbean355.admiralbulldog.assets.SoundBite
import com.github.mrbean355.admiralbulldog.assets.SoundBites
import com.github.mrbean355.admiralbulldog.common.PlayIconPainter
import com.github.mrbean355.admiralbulldog.common.WINDOW_WIDTH
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.common.showInformation
import com.github.mrbean355.admiralbulldog.ui.openComposeScreen
import com.github.mrbean355.admiralbulldog.ui.theme.BulldogTheme
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SyncSoundBitesScreen(viewModel: SyncSoundBitesViewModel) {
    val progress by viewModel.progress.collectAsState()
    val isFinished by viewModel.isFinished.collectAsState()
    val syncResult by viewModel.syncResult.collectAsState()
    val showError by viewModel.showError.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.showNoUpdates.collectLatest {
            showInformation(getString("header_latest_sound_bites"), getString("content_latest_sound_bites"))
        }
    }

    if (showError) {
        AlertDialog(
            onDismissRequest = { viewModel.onErrorDismissed() },
            title = { Text(getString("header_unknown_error")) },
            text = { Text(getString("content_update_check_failed")) },
            confirmButton = {
                Button(onClick = { viewModel.updateSounds() }) {
                    Text(getString("btn_retry"))
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onErrorDismissed() }) {
                    Text(getString("btn_cancel"))
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .width(WINDOW_WIDTH.dp)
            .height(500.dp)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (!isFinished) {
            SyncingState(progress, onCancel = { viewModel.onCancelClicked() })
        } else if (syncResult != null) {
            ResultsState(syncResult!!, onDone = { viewModel.onDoneClicked() })
        }
    }
}

@Composable
private fun SyncingState(progress: Float, onCancel: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (progress >= 0) {
            Text(
                text = "${(progress * 100).toInt()}%",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            LinearProgressIndicator(
                progress = { progress.coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onCancel) {
            Text(getString("btn_cancel"))
        }
    }
}

@Composable
private fun ResultsState(result: SoundBites.SyncResult, onDone: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.weight(1f)) {
            item { CategoryHeader(getString("label_new_sounds"), result.newSounds) }
            item { CategoryHeader(getString("label_changed_sounds"), result.changedSounds) }
            item { CategoryHeader(getString("label_deleted_sounds"), result.deletedSounds) }
            item { CategoryHeader(getString("label_failed_sounds"), result.failedSounds) }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onDone,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(getString("btn_done"))
        }
    }
}

@Composable
private fun CategoryHeader(label: String, items: Collection<String>) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Surface(
            modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded },
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = "$label (${items.size})",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null
                )
            }
        }
        if (expanded) {
            items.forEach { soundName ->
                val sound = SoundBites.findSound(soundName)
                SoundItem(soundName, sound)
            }
        }
    }
}

@Composable
private fun SoundItem(name: String, sound: SoundBite?) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, top = 4.dp, bottom = 4.dp)
    ) {
        Text(name, modifier = Modifier.weight(1f))
        if (sound != null) {
            IconButton(
                onClick = { sound.play() },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    painter = PlayIconPainter(),
                    contentDescription = getString("tooltip_play_locally"),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Preview
@Composable
private fun SyncSoundBitesScreenPreview() {
    BulldogTheme {
        Surface {
            SyncSoundBitesScreen(SyncSoundBitesViewModel())
        }
    }
}

fun openSyncSoundBitesScreen() {
    openComposeScreen(
        title = getString("sync_sound_bites_title"),
        viewModelFactory = { SyncSoundBitesViewModel() }
    ) { viewModel ->
        SyncSoundBitesScreen(viewModel)
    }
}
