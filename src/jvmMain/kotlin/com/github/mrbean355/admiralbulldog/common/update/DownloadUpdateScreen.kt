package com.github.mrbean355.admiralbulldog.common.update

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.mrbean355.admiralbulldog.arch.AssetInfo
import com.github.mrbean355.admiralbulldog.common.WINDOW_WIDTH
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.ui.openComposeScreen

@Composable
fun DownloadUpdateScreen(viewModel: DownloadUpdateViewModel) {
    val header by viewModel.header.collectAsState()
    val progress by viewModel.progress.collectAsState()

    Column(
        modifier = Modifier
            .width(WINDOW_WIDTH.dp)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = header,
            style = MaterialTheme.typography.titleMedium
        )

        if (progress == null) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        } else {
            LinearProgressIndicator(
                progress = { progress!! },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Button(
            onClick = { viewModel.requestWindowClose() },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(getString("btn_cancel"))
        }
    }
}

@androidx.compose.desktop.ui.tooling.preview.Preview
@Composable
private fun DownloadUpdateScreenPreview() {
    MaterialTheme {
        androidx.compose.material3.Surface {
            DownloadUpdateScreen(
                DownloadUpdateViewModel(
                    assetInfo = AssetInfo("bulldog.jar", "https://example.com"),
                    destination = ".",
                    onSuccess = {},
                    onCancel = {}
                )
            )
        }
    }
}

fun openDownloadUpdateScreen(
    assetInfo: AssetInfo,
    destination: String,
    onSuccess: () -> Unit,
    onCancel: () -> Unit = {}
) {
    openComposeScreen(
        title = getString("title_app"),
        viewModelFactory = {
            DownloadUpdateViewModel(assetInfo, destination, onSuccess, onCancel)
        }
    ) { viewModel ->
        DownloadUpdateScreen(viewModel)
    }
}
