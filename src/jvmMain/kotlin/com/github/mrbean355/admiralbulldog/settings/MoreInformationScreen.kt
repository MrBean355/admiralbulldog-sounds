package com.github.mrbean355.admiralbulldog.settings

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.github.mrbean355.admiralbulldog.common.URL_BULLDOG_TWITCH
import com.github.mrbean355.admiralbulldog.common.URL_DISCORD_SERVER_INVITE
import com.github.mrbean355.admiralbulldog.common.URL_PAYPAL
import com.github.mrbean355.admiralbulldog.common.URL_PROJECT_WEBSITE
import com.github.mrbean355.admiralbulldog.common.URL_TELEGRAM_CHANNEL
import com.github.mrbean355.admiralbulldog.common.URL_TWITCH_CHANNEL
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.ui.openComposeScreen
import com.github.mrbean355.admiralbulldog.ui.theme.BulldogTheme

@Composable
fun MoreInformationScreen(viewModel: MoreInformationViewModel) {
    Column(
        modifier = Modifier.padding(24.dp).width(450.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(getString("label_more_information_project_dev"), style = MaterialTheme.typography.titleMedium)

        TextButton(onClick = { viewModel.openUrl(URL_PROJECT_WEBSITE) }) {
            Text(getString("btn_project_website"))
        }
        TextButton(onClick = { viewModel.openUrl(URL_TELEGRAM_CHANNEL) }) {
            Text(getString("btn_telegram_channel"))
        }
        TextButton(onClick = { viewModel.openUrl(URL_TWITCH_CHANNEL) }) {
            Text(getString("btn_twitch_channel"))
        }

        Text(getString("label_need_help"), style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 8.dp))
        TextButton(onClick = { viewModel.openUrl(URL_DISCORD_SERVER_INVITE) }) {
            Text(getString("btn_discord_community"))
        }

        Text(getString("label_more_information_donate"), style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 8.dp))
        TextButton(onClick = { viewModel.openUrl(URL_PAYPAL) }) {
            Text(getString("btn_paypal"))
        }

        Text(
            text = getString("label_not_affiliated"),
            style = MaterialTheme.typography.bodyMedium,
            fontStyle = FontStyle.Italic,
            modifier = Modifier.padding(top = 8.dp)
        )
        TextButton(onClick = { viewModel.openUrl(URL_BULLDOG_TWITCH) }) {
            Text(getString("btn_bulldog_twitch"))
        }
    }
}

@Preview
@Composable
private fun MoreInformationScreenPreview() {
    BulldogTheme {
        Surface {
            MoreInformationScreen(MoreInformationViewModel())
        }
    }
}

fun openMoreInformationScreen() {
    openComposeScreen(
        title = getString("title_more_information"),
        viewModelFactory = { MoreInformationViewModel() }
    ) { viewModel ->
        MoreInformationScreen(viewModel)
    }
}
