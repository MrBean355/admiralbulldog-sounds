package com.github.mrbean355.admiralbulldog.sounds

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.mrbean355.admiralbulldog.common.MAX_RATE
import com.github.mrbean355.admiralbulldog.common.MIN_RATE
import com.github.mrbean355.admiralbulldog.common.PlayIconPainter
import com.github.mrbean355.admiralbulldog.common.WINDOW_WIDTH
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.ui.components.NumericSpinner
import com.github.mrbean355.admiralbulldog.ui.openComposeScreen
import com.github.mrbean355.admiralbulldog.ui.theme.BulldogTheme

@Composable
fun TestPlaybackSpeedScreen(viewModel: TestPlaybackSpeedViewModel) {
    val playbackRate by viewModel.playbackRate.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filteredSounds by viewModel.filteredSounds.collectAsState()

    Column(
        modifier = Modifier
            .width(WINDOW_WIDTH.dp)
            .height(600.dp)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        NumericSpinner(
            label = getString("label_playback_speed"),
            value = playbackRate,
            range = MIN_RATE..MAX_RATE,
            onValueChange = { viewModel.onRateChanged(it) }
        )

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.onSearchQueryChanged(it) },
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
                items(filteredSounds, key = { it.name }) { sound ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = sound.name,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = { sound.play(rate = playbackRate) },
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
    }
}

@Preview
@Composable
private fun TestPlaybackSpeedScreenPreview() {
    BulldogTheme {
        Surface {
            TestPlaybackSpeedScreen(TestPlaybackSpeedViewModel())
        }
    }
}

fun openTestPlaybackSpeedScreen() {
    openComposeScreen(
        title = getString("title_test_playback"),
        viewModelFactory = { TestPlaybackSpeedViewModel() }
    ) { viewModel ->
        TestPlaybackSpeedScreen(viewModel)
    }
}