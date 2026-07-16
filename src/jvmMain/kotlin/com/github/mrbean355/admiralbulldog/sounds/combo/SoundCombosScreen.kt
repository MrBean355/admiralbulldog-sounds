package com.github.mrbean355.admiralbulldog.sounds.combo

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.mrbean355.admiralbulldog.assets.ComboSoundBite
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.ui.openComposeScreen
import com.github.mrbean355.admiralbulldog_sounds.generated.resources.Res
import com.github.mrbean355.admiralbulldog_sounds.generated.resources.add
import com.github.mrbean355.admiralbulldog_sounds.generated.resources.delete
import com.github.mrbean355.admiralbulldog_sounds.generated.resources.help
import com.github.mrbean355.admiralbulldog_sounds.generated.resources.play
import org.jetbrains.compose.resources.painterResource

@Composable
fun SoundCombosScreen(viewModel: SoundCombosViewModel) {
    val items by viewModel.items.collectAsState()
    val selectedItem by viewModel.selectedItem.collectAsState()

    Column(
        modifier = Modifier.padding(16.dp).width(400.dp).height(500.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(modifier = Modifier.weight(1f)) {
            val listState = rememberLazyListState()
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize()
            ) {
                items(items) { item ->
                    ComboItem(
                        item = item,
                        isSelected = item == selectedItem,
                        onClick = { viewModel.onItemSelected(item) },
                        onDoubleClick = { viewModel.onEditClicked(item) }
                    )
                }
            }
            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                adapter = rememberScrollbarAdapter(listState)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(onClick = { viewModel.onHelpClicked() }) {
                Icon(painterResource(Res.drawable.help), contentDescription = null)
            }

            Spacer(modifier = Modifier.weight(1f))

            IconButton(
                onClick = { viewModel.onRemoveClicked() },
                enabled = selectedItem != null
            ) {
                Icon(painterResource(Res.drawable.delete), contentDescription = null)
            }

            IconButton(onClick = { viewModel.onAddClicked() }) {
                Icon(painterResource(Res.drawable.add), contentDescription = null)
            }
        }
    }
}

@Composable
private fun ComboItem(
    item: ComboSoundBite,
    isSelected: Boolean,
    onClick: () -> Unit,
    onDoubleClick: () -> Unit
) {
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        Color.Transparent
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        IconButton(
            onClick = { item.play() },
            modifier = Modifier.size(24.dp)
        ) {
            Icon(painterResource(Res.drawable.play), contentDescription = getString("tooltip_play_locally"))
        }
        Text(text = item.name, modifier = Modifier.weight(1f))
    }
}

fun openSoundCombosScreen(onDone: () -> Unit = {}) {
    openComposeScreen(
        title = getString("title_sound_combos"),
        viewModelFactory = { SoundCombosViewModel() },
        onCloseRequest = onDone
    ) { viewModel ->
        SoundCombosScreen(viewModel)
    }
}