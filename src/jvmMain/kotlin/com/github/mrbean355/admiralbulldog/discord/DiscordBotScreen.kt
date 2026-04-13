package com.github.mrbean355.admiralbulldog.discord

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.github.mrbean355.admiralbulldog.common.GreenDotIconPainter
import com.github.mrbean355.admiralbulldog.common.GreyDotIconPainter
import com.github.mrbean355.admiralbulldog.common.RedDotIconPainter
import com.github.mrbean355.admiralbulldog.common.URL_DISCORD_BOT_INVITE
import com.github.mrbean355.admiralbulldog.common.URL_DISCORD_WIKI
import com.github.mrbean355.admiralbulldog.common.URL_DISCORD_WIKI_COMMANDS
import com.github.mrbean355.admiralbulldog.common.YellowDotIconPainter
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.sounds.friendlyName
import com.github.mrbean355.admiralbulldog.triggers.SOUND_TRIGGER_TYPES
import com.github.mrbean355.admiralbulldog.ui.components.LabeledCheckbox
import com.github.mrbean355.admiralbulldog.ui.openComposeScreen
import com.github.mrbean355.admiralbulldog.ui.theme.BulldogTheme
import java.awt.Desktop
import java.net.URI

@Composable
fun DiscordBotScreen(viewModel: DiscordBotViewModel) {
    val botEnabled by viewModel.botEnabled.collectAsState()
    val token by viewModel.token.collectAsState()
    val status by viewModel.status.collectAsState()
    val statusType by viewModel.statusType.collectAsState()

    Column(
        modifier = Modifier.padding(24.dp).width(600.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LabeledCheckbox(
                label = getString("label_enable_discord_bot"),
                checked = botEnabled,
            ) { viewModel.botEnabled.value = it }

            Text(
                text = getString("label_invite_discord_bot"),
                color = MaterialTheme.colorScheme.primary,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable { openUrl(URL_DISCORD_BOT_INVITE) }
            )
        }

        OutlinedTextField(
            value = token,
            onValueChange = { viewModel.token.value = it },
            label = { Text(getString("prompt_magic_number")) },
            modifier = Modifier.fillMaxWidth(),
            enabled = botEnabled,
            singleLine = true
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val painter = when (statusType) {
                DiscordBotViewModel.Status.NEUTRAL -> GreyDotIconPainter()
                DiscordBotViewModel.Status.GOOD -> GreenDotIconPainter()
                DiscordBotViewModel.Status.BAD -> RedDotIconPainter()
                DiscordBotViewModel.Status.LOADING -> YellowDotIconPainter()
            }
            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Text(status, style = MaterialTheme.typography.bodyMedium)
        }

        Text(
            text = getString("label_play_through_discord"),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 8.dp)
        )

        // Trigger Grid (3 columns)
        val types = SOUND_TRIGGER_TYPES.toList()
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            for (i in types.indices step 3) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    for (j in 0 until 3) {
                        val index = i + j
                        if (index < types.size) {
                            val type = types[index]
                            val checked by viewModel.getTriggerToggle(type).collectAsState()
                            LabeledCheckbox(
                                label = type.friendlyName,
                                checked = checked,
                                enabled = botEnabled,
                                modifier = Modifier.weight(1f)
                            ) { viewModel.getTriggerToggle(type).value = it }
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
        ) {
            Button(onClick = { openUrl(URL_DISCORD_WIKI_COMMANDS) }) {
                Text(getString("btn_discord_bot_commands"))
            }
            Button(
                onClick = { openSoundBoardScreen() },
                enabled = botEnabled
            ) {
                Text(getString("action_sound_board"))
            }
            Button(onClick = { openUrl(URL_DISCORD_WIKI) }) {
                Text(getString("btn_help"))
            }
        }
    }
}

private fun openUrl(url: String) {
    Desktop.getDesktop().browse(URI(url))
}


@Preview
@Composable
private fun DiscordBotScreenPreview() {
    BulldogTheme {
        Surface {
            DiscordBotScreen(DiscordBotViewModel())
        }
    }
}

fun openDiscordBotScreen() {
    openComposeScreen(
        title = getString("title_discord_bot"),
        viewModelFactory = { DiscordBotViewModel() }
    ) { viewModel ->
        DiscordBotScreen(viewModel)
    }
}