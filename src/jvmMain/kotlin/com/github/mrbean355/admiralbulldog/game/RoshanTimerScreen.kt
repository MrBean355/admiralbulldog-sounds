package com.github.mrbean355.admiralbulldog.game

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.ui.openComposeScreen

@Composable
fun RoshanTimerScreen(viewModel: RoshanTimerViewModel) {
    val deathTime by viewModel.deathTime.collectAsState()
    val aegisExpiryTime by viewModel.aegisExpiryTime.collectAsState()
    val respawnTime by viewModel.respawnTime.collectAsState()
    val aegisExpiryTimeTurbo by viewModel.aegisExpiryTimeTurbo.collectAsState()
    val respawnTimeTurbo by viewModel.respawnTimeTurbo.collectAsState()

    Column(
        modifier = Modifier.padding(24.dp).width(450.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(deathTime, style = MaterialTheme.typography.bodyLarge)
        Text(aegisExpiryTime, style = MaterialTheme.typography.bodyLarge)
        Text(respawnTime, style = MaterialTheme.typography.bodyLarge)

        Text(
            text = getString("label_roshan_timer_turbo"),
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(top = 8.dp)
        )
        Text(aegisExpiryTimeTurbo, style = MaterialTheme.typography.bodyLarge)
        Text(respawnTimeTurbo, style = MaterialTheme.typography.bodyLarge)

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = { viewModel.onHelpClicked() }) {
                Text(getString("btn_help"))
            }
        }
    }
}

@Preview
@Composable
private fun RoshanTimerScreenPreview() {
    MaterialTheme {
        Surface {
            RoshanTimerScreen(RoshanTimerViewModel())
        }
    }
}

fun openRoshanTimerScreen() {
    openComposeScreen(
        title = getString("header_roshan_timer"),
        viewModelFactory = { RoshanTimerViewModel() }
    ) { viewModel ->
        RoshanTimerScreen(viewModel)
    }
}
