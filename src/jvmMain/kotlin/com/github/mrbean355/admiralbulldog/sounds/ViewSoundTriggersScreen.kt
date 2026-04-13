package com.github.mrbean355.admiralbulldog.sounds

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.mrbean355.admiralbulldog.common.HelpIconPainter
import com.github.mrbean355.admiralbulldog.common.WINDOW_WIDTH
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.triggers.SOUND_TRIGGER_TYPES
import com.github.mrbean355.admiralbulldog.ui.openComposeScreen
import com.github.mrbean355.admiralbulldog.ui.theme.BulldogTheme

@Composable
fun ViewSoundTriggersScreen(viewModel: ViewSoundTriggersViewModel) {
    val triggerStates by viewModel.triggerStates.collectAsState()

    Column(
        modifier = Modifier
            .width(WINDOW_WIDTH.dp)
            .height(600.dp)
            .padding(16.dp)
    ) {
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            val listState = rememberLazyListState()
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize()
            ) {
                items(SOUND_TRIGGER_TYPES.toList()) { type ->
                    val state = triggerStates[type] ?: ViewSoundTriggersViewModel.TriggerState("", false)
                    TriggerItem(
                        text = state.text,
                        isActive = state.isActive,
                        onClick = { viewModel.onConfigureClicked(type) }
                    )
                }
            }
            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                adapter = rememberScrollbarAdapter(listState)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = { viewModel.onManageSoundsClicked() }) {
                Text(getString("btn_manage_sounds"))
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = { viewModel.onHelpClicked() }) {
                Icon(
                    painter = HelpIconPainter(),
                    contentDescription = getString("header_about_sound_triggers"),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun TriggerItem(text: String, isActive: Boolean, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    val textColor = if (isActive) {
        MaterialTheme.colorScheme.onSurface
    } else {
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
    }

    val backgroundColor = when {
        isHovered && isActive -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
        isHovered && !isActive -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        else -> Color.Transparent
    }

    Text(
        text = text,
        color = textColor,
        modifier = Modifier
            .fillMaxWidth()
            .hoverable(interactionSource)
            .clickable(onClick = onClick)
            .background(backgroundColor)
            .padding(vertical = 12.dp, horizontal = 16.dp)
    )
}


@Preview
@Composable
private fun ViewSoundTriggersScreenPreview() {
    BulldogTheme {
        Surface {
            ViewSoundTriggersScreen(ViewSoundTriggersViewModel())
        }
    }
}

fun openViewSoundTriggersScreen() {
    openComposeScreen(
        title = getString("title_toggle_sound_triggers"),
        viewModelFactory = { ViewSoundTriggersViewModel() }
    ) { viewModel ->
        ViewSoundTriggersScreen(viewModel)
    }
}
