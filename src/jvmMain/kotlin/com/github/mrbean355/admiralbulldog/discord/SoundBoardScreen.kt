package com.github.mrbean355.admiralbulldog.discord

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.github.mrbean355.admiralbulldog.common.MAX_RATE
import com.github.mrbean355.admiralbulldog.common.MIN_RATE
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.ui.openComposeScreen
import com.github.mrbean355.admiralbulldog.ui.theme.BulldogTheme
import kotlin.math.roundToInt

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SoundBoardScreen(viewModel: SoundBoardViewModel) {
    val playbackRate by viewModel.playbackRate.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val soundBoard by viewModel.soundBoard.collectAsState()
    val isEmpty by viewModel.isEmpty.collectAsState()
    val emptyMessage by viewModel.emptyMessage.collectAsState()

    Column(
        modifier = Modifier.padding(24.dp).width(600.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column {
            Text(getString("label_sound_board_description_1"), style = MaterialTheme.typography.bodyLarge)
            Text(getString("label_sound_board_description_2"), style = MaterialTheme.typography.bodyLarge)
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(getString("label_playback_speed"), modifier = Modifier.padding(end = 16.dp))
            Slider(
                value = playbackRate.toFloat(),
                onValueChange = { viewModel.playbackRate.value = it.roundToInt() },
                valueRange = MIN_RATE.toFloat()..MAX_RATE.toFloat(),
                modifier = Modifier.width(150.dp)
            )
            Text("$playbackRate%", modifier = Modifier.padding(start = 8.dp, end = 24.dp))

            Spacer(modifier = Modifier.weight(1f))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.searchQuery.value = it },
                label = { Text(getString("prompt_search")) },
                singleLine = true,
                modifier = Modifier.width(200.dp)
            )
        }

        if (isEmpty) {
            Text(
                text = emptyMessage,
                style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic)
            )
        }

        Box(modifier = Modifier.fillMaxWidth().height(300.dp)) {
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier.verticalScroll(scrollState).padding(end = 16.dp)
            ) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    soundBoard.forEach { soundBite ->
                        Button(onClick = { viewModel.onSoundClicked(soundBite) }) {
                            Text(soundBite.name)
                        }
                    }
                }
            }
            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                adapter = rememberScrollbarAdapter(scrollState)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Button(
                onClick = {
                    openConfigureSoundBoardScreen {
                        viewModel.refresh()
                    }
                }
            ) {
                Text(getString("btn_customise"))
            }
        }
    }
}


@Preview
@Composable
private fun SoundBoardScreenPreview() {
    BulldogTheme {
        Surface {
            SoundBoardScreen(SoundBoardViewModel())
        }
    }
}

fun openSoundBoardScreen() {
    openComposeScreen(
        title = getString("title_sound_board"),
        viewModelFactory = { SoundBoardViewModel() }
    ) { viewModel ->
        SoundBoardScreen(viewModel)
    }
}
