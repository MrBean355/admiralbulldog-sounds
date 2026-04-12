package com.github.mrbean355.admiralbulldog.sounds.manager

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
import androidx.compose.ui.unit.dp
import com.github.mrbean355.admiralbulldog.common.AddIconPainter
import com.github.mrbean355.admiralbulldog.common.DeleteIconPainter
import com.github.mrbean355.admiralbulldog.common.HelpIconPainter
import com.github.mrbean355.admiralbulldog.common.Volume
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.ui.openComposeScreen

@Composable
fun VolumeManagerScreen(viewModel: VolumeManagerViewModel) {
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
                    VolumeItem(
                        item = item,
                        isSelected = item == selectedItem,
                        onClick = { viewModel.onItemSelected(item) },
                        onDoubleClick = { viewModel.onEditVolumeClicked(item) }
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
                Icon(HelpIconPainter(), contentDescription = null)
            }

            Spacer(modifier = Modifier.weight(1f))

            IconButton(
                onClick = { viewModel.onRemoveVolumeClicked() },
                enabled = selectedItem != null
            ) {
                Icon(DeleteIconPainter(), contentDescription = null)
            }

            IconButton(onClick = { viewModel.onAddVolumeClicked() }) {
                Icon(AddIconPainter(), contentDescription = null)
            }
        }
    }
}

@Composable
private fun VolumeItem(
    item: Volume,
    isSelected: Boolean,
    onClick: () -> Unit,
    onDoubleClick: () -> Unit
) {
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        androidx.compose.ui.graphics.Color.Transparent
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(item.name)
        Text("${item.volume}%")
    }
}

fun openVolumeManagerScreen() {
    openComposeScreen(
        title = getString("title_volume_manager"),
        viewModelFactory = { VolumeManagerViewModel() }
    ) { viewModel ->
        VolumeManagerScreen(viewModel)
    }
}
