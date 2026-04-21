package com.github.mrbean355.admiralbulldog.home

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.github.mrbean355.admiralbulldog.APP_VERSION
import com.github.mrbean355.admiralbulldog.common.AlertButton
import com.github.mrbean355.admiralbulldog.common.PauseChampIconPainter
import com.github.mrbean355.admiralbulldog.common.PoggiesIconPainter
import com.github.mrbean355.admiralbulldog.common.URL_APP_INSTALLATION
import com.github.mrbean355.admiralbulldog.common.URL_SPECIFIC_RELEASE
import com.github.mrbean355.admiralbulldog.common.browseUrl
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.common.showInformation
import com.github.mrbean355.admiralbulldog.ui.openComposeScreen
import com.github.mrbean355.admiralbulldog.ui.theme.BulldogTheme

@Composable
fun MainScreen(viewModel: MainViewModel) {
    val isConnected by viewModel.isConnected.collectAsState()

    Box(
        modifier = Modifier
            .width(600.dp)
            .height(550.dp)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Status Icon
            val iconPainter = if (isConnected) PoggiesIconPainter() else PauseChampIconPainter()
            Image(
                painter = iconPainter,
                contentDescription = null,
                modifier = Modifier.size(96.dp)
            )

            // Heading
            Text(
                text = if (isConnected) getString("msg_connected") else getString("msg_not_connected"),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = if (isConnected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )

            // Progress Bar / Info
            if (!isConnected) {
                LinearProgressIndicator(modifier = Modifier.width(200.dp))
                Text(getString("dsc_not_connected"))
                Text(
                    text = getString("btn_not_working"),
                    color = MaterialTheme.colorScheme.primary,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable {
                        showInformation(
                            getString("header_gsi_launch_option"),
                            getString("content_gsi_launch_option"),
                            AlertButton.MORE_INFO,
                            AlertButton.OK
                        ) { action ->
                            if (action == AlertButton.MORE_INFO) {
                                browseUrl(URL_APP_INSTALLATION)
                            }
                        }
                    }
                )
            } else {
                Text(getString("dsc_connected"))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Navigation Buttons
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = { viewModel.onChangeSoundsClicked() },
                        modifier = Modifier.width(160.dp).height(48.dp)
                    ) {
                        Text(getString("btn_change_sounds"))
                    }
                    Button(
                        onClick = { viewModel.onDiscordBotClicked() },
                        modifier = Modifier.width(160.dp).height(48.dp)
                    ) {
                        Text(getString("btn_discord_bot"))
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = { viewModel.onDotaModClicked() },
                        modifier = Modifier.width(160.dp).height(48.dp)
                    ) {
                        Text(getString("btn_dota_mods"))
                    }
                    Button(
                        onClick = { viewModel.onRoshanTimerClicked() },
                        modifier = Modifier.width(160.dp).height(48.dp)
                    ) {
                        Text(getString("btn_roshan_timer"))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Version
            Text(
                text = viewModel.version,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable {
                    browseUrl(URL_SPECIFIC_RELEASE.format(APP_VERSION.value))
                }
            )
        }

        // Settings Button
        FloatingActionButton(
            onClick = { viewModel.onSettingsClicked() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ) {
            Icon(Icons.Default.Settings, contentDescription = getString("tooltip_settings"))
        }
    }
}

@Preview
@Composable
private fun MainScreenPreview() {
    BulldogTheme {
        Surface {
            MainScreen(MainViewModel())
        }
    }
}

fun openMainScreen() {
    openComposeScreen(
        title = getString("title_app"),
        viewModelFactory = { MainViewModel() },
        escapeClosesWindow = false
    ) { viewModel ->
        MainScreen(viewModel)
    }
}
