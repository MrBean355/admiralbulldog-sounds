package com.github.mrbean355.admiralbulldog.sounds.manager

import androidx.compose.foundation.HorizontalScrollbar
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.mrbean355.admiralbulldog.assets.SoundBite
import com.github.mrbean355.admiralbulldog.common.PlayIconPainter
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.sounds.tableHeader
import com.github.mrbean355.admiralbulldog.triggers.SOUND_TRIGGER_TYPES
import com.github.mrbean355.admiralbulldog.ui.openComposeScreen

@Composable
fun SoundManagerScreen(viewModel: SoundManagerViewModel) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val showPlaySounds by viewModel.showPlaySounds.collectAsState()
    val showCombos by viewModel.showCombos.collectAsState()
    val showUsed by viewModel.showUsed.collectAsState()
    val showUnused by viewModel.showUnused.collectAsState()
    val items by viewModel.items.collectAsState()

    Column(
        modifier = Modifier.size(width = 1000.dp, height = 600.dp).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Search & Filters
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                modifier = Modifier.width(200.dp),
                placeholder = { Text(getString("prompt_search")) },
                singleLine = true
            )

            FilterCheckbox(getString("label_filter_play_sounds"), showPlaySounds) { viewModel.onFilterChanged(SoundManagerViewModel.Filter.PLAY_SOUNDS, it) }
            FilterCheckbox(getString("label_filter_sound_combos"), showCombos) { viewModel.onFilterChanged(SoundManagerViewModel.Filter.COMBOS, it) }
            FilterCheckbox(getString("label_filter_used"), showUsed) { viewModel.onFilterChanged(SoundManagerViewModel.Filter.USED, it) }
            FilterCheckbox(getString("label_filter_unused"), showUnused) { viewModel.onFilterChanged(SoundManagerViewModel.Filter.UNUSED, it) }
        }

        // Table
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            val horizontalScrollState = rememberScrollState()
            val verticalScrollState = rememberLazyListState()

            Column(modifier = Modifier.fillMaxSize().horizontalScroll(horizontalScrollState)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = getString("column_sound_bite"),
                        modifier = Modifier.width(200.dp).padding(8.dp),
                        fontWeight = FontWeight.Bold
                    )
                    SOUND_TRIGGER_TYPES.forEach { trigger ->
                        Text(
                            text = trigger.tableHeader,
                            modifier = Modifier.width(60.dp).padding(8.dp),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }

                // Body
                Box(modifier = Modifier.weight(1f)) {
                    LazyColumn(
                        state = verticalScrollState,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(items) { sound ->
                            SoundRow(sound, viewModel)
                        }
                    }
                }
            }

            // Scrollbars
            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                adapter = rememberScrollbarAdapter(verticalScrollState)
            )
            HorizontalScrollbar(
                modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
                adapter = rememberScrollbarAdapter(horizontalScrollState)
            )
        }

        // Footer
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(onClick = { viewModel.onManageVolumesClicked() }) {
                Text(getString("btn_volume_manager"))
            }
            Button(onClick = { viewModel.onSoundCombosClicked() }) {
                Text(getString("btn_sound_combos"))
            }
        }
    }
}

@Composable
private fun FilterCheckbox(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked = checked, onCheckedChange = onCheckedChange)
        Text(text = label, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun SoundRow(sound: SoundBite, viewModel: SoundManagerViewModel) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.width(200.dp).padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(onClick = { sound.play() }, modifier = Modifier.size(24.dp)) {
                Icon(PlayIconPainter(), contentDescription = null)
            }
            Text(text = sound.name, style = MaterialTheme.typography.bodyMedium)
        }

        SOUND_TRIGGER_TYPES.forEach { trigger ->
            var checked by remember(sound, trigger) { mutableStateOf(viewModel.isTriggerSelected(trigger, sound)) }
            Box(
                modifier = Modifier.width(60.dp),
                contentAlignment = Alignment.Center
            ) {
                Checkbox(
                    checked = checked,
                    onCheckedChange = {
                        checked = it
                        viewModel.onToggleTrigger(trigger, sound, it)
                    }
                )
            }
        }
    }
}

fun openSoundManagerScreen() {
    openComposeScreen(
        title = getString("title_sound_bite_manager"),
        viewModelFactory = { SoundManagerViewModel() }
    ) { viewModel ->
        SoundManagerScreen(viewModel)
    }
}
