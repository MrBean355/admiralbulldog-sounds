package com.github.mrbean355.admiralbulldog.mods

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.github.mrbean355.admiralbulldog.common.HelpIconPainter
import com.github.mrbean355.admiralbulldog.common.MonkaGigaIconPainter
import com.github.mrbean355.admiralbulldog.common.WINDOW_WIDTH
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.ui.openComposeScreen

@Composable
fun DotaModsScreen(viewModel: DotaModsViewModel) {
    val showProgress by viewModel.showProgress.collectAsState()
    val items by viewModel.items.collectAsState()
    val showRiskDisclaimer by viewModel.showRiskDisclaimer.collectAsState()
    val isUpdating by viewModel.isUpdating.collectAsState()

    if (showRiskDisclaimer) {
        AcceptModRiskDialog(
            onAccepted = { viewModel.onRiskAccepted() },
            onRejected = { viewModel.onRiskRejected() }
        )
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .width(WINDOW_WIDTH.dp)
            .height(700.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(getString("label_enable_mods_manually"), modifier = Modifier.weight(1f))
            Button(onClick = { viewModel.onEnableClicked() }) {
                Text(getString("btn_enable"))
            }
        }

        Text(
            text = getString("label_choose_mods"),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        if (showProgress) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            val listState = rememberLazyListState()
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize().padding(end = 12.dp)
            ) {
                items(items, key = { it.name }) { mod ->
                    val checkedState by viewModel.getCheckedState(mod).collectAsState()
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.getCheckedState(mod).value = !checkedState }
                            .padding(vertical = 4.dp, horizontal = 8.dp)
                    ) {
                        Checkbox(
                            checked = checkedState,
                            onCheckedChange = { viewModel.getCheckedState(mod).value = it }
                        )
                        Text(
                            text = mod.name,
                            modifier = Modifier.weight(1f).padding(start = 8.dp)
                        )
                        IconButton(onClick = { viewModel.onAboutModClicked(mod) }) {
                            Icon(
                                painter = HelpIconPainter(),
                                contentDescription = getString("tooltip_more_info"),
                                modifier = Modifier.size(20.dp)
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
                modifier = Modifier.clickable { viewModel.onSelectAllClicked() }
            )
            Text(
                text = getString("btn_deselect_all"),
                color = MaterialTheme.colorScheme.primary,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable { viewModel.onDeselectAllClicked() }
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = getString("btn_about_modding"),
                color = MaterialTheme.colorScheme.primary,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable { viewModel.onAboutModdingClicked() }
            )
        }

        Button(
            onClick = { viewModel.onInstallClicked() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(getString("btn_download"))
        }
    }

    if (isUpdating) {
        Box(
            modifier = Modifier.fillMaxSize().clickable(enabled = false) {},
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
            ) {}
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CircularProgressIndicator()
                Text(getString("label_loading"), style = MaterialTheme.typography.titleLarge)
            }
        }
    }
}

@Composable
private fun AcceptModRiskDialog(onAccepted: () -> Unit, onRejected: () -> Unit) {
    var accepted by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onRejected,
        title = { Text(getString("title_mods_risk")) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = MonkaGigaIconPainter(),
                        contentDescription = null,
                        modifier = Modifier.size(64.dp)
                    )
                    Text(
                        text = getString("description_mods_risk"),
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { accepted = !accepted }
                ) {
                    Checkbox(
                        checked = accepted,
                        onCheckedChange = { accepted = it }
                    )
                    Text(
                        text = getString("label_accept_mods_risk"),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onAccepted,
                enabled = accepted
            ) {
                Text(getString("btn_done"))
            }
        },
        dismissButton = {
            TextButton(onClick = onRejected) {
                Text(getString("btn_cancel"))
            }
        }
    )
}

@Preview
@Composable
private fun DotaModsScreenPreview() {
    MaterialTheme {
        Surface {
            DotaModsScreen(DotaModsViewModel())
        }
    }
}

fun openDotaModsScreen() {
    openComposeScreen(
        title = getString("title_mods"),
        viewModelFactory = { DotaModsViewModel() }
    ) { viewModel ->
        DotaModsScreen(viewModel)
    }
}
