package com.github.mrbean355.admiralbulldog.discord

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.github.mrbean355.admiralbulldog.common.PlayIconPainter
import com.github.mrbean355.admiralbulldog.common.WINDOW_WIDTH
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.ui.openComposeScreen
import com.github.mrbean355.admiralbulldog.ui.theme.BulldogTheme

@Composable
fun ConfigureSoundBoardScreen(viewModel: ConfigureSoundBoardViewModel, onCloseRequest: () -> Unit) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filteredItems by viewModel.filteredItems.collectAsState()
    val soundToggles by viewModel.soundToggles.collectAsState()

    Column(
        modifier = Modifier.padding(16.dp).width(WINDOW_WIDTH.dp).height(800.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.searchQuery.value = it },
            label = { Text(getString("prompt_search")) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            val listState = rememberLazyListState()
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize().padding(end = 12.dp)
            ) {
                items(filteredItems, key = { it.name }) { soundBite ->
                    val isChecked = soundToggles[soundBite] ?: false
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.toggleSound(soundBite, !isChecked) }
                            .padding(vertical = 4.dp, horizontal = 8.dp)
                    ) {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { viewModel.toggleSound(soundBite, it) }
                        )
                        Text(
                            text = soundBite.name,
                            modifier = Modifier.weight(1f).padding(start = 8.dp)
                        )
                        IconButton(
                            onClick = { viewModel.onPlayClicked(soundBite) },
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
            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                adapter = rememberScrollbarAdapter(listState)
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(getString("label_select"))
            Text(
                text = getString("btn_select_all"),
                color = MaterialTheme.colorScheme.primary,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable { viewModel.onSelectAll() }
            )
            Text(
                text = getString("btn_deselect_all"),
                color = MaterialTheme.colorScheme.primary,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable { viewModel.onDeselectAll() }
            )
            Spacer(modifier = Modifier.weight(1f))
            Button(onClick = { viewModel.onSave(onCloseRequest) }) {
                Text(getString("btn_save"))
            }
        }
    }
}

@Preview
@Composable
private fun ConfigureSoundBoardScreenPreview() {
    BulldogTheme {
        Surface {
            ConfigureSoundBoardScreen(ConfigureSoundBoardViewModel()) {}
        }
    }
}

fun openConfigureSoundBoardScreen(onClose: () -> Unit) {
    openComposeScreen(
        title = getString("title_customise_sound_board"),
        viewModelFactory = { ConfigureSoundBoardViewModel() }
    ) { viewModel ->
        ConfigureSoundBoardScreen(viewModel, onClose)
    }
}