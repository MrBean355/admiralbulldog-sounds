package com.github.mrbean355.admiralbulldog.installation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.github.mrbean355.admiralbulldog.common.getString

@Composable
fun ChooseDotaDirectoryStep(viewModel: InstallationWizardViewModel) {
    val dotaPath by viewModel.dotaPath.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(getString("install_path_name"))

        Text(
            text = dotaPath,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth()
        )

        if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Button(onClick = { viewModel.onChooseDirectoryClicked() }) {
            Text(getString("install_choose"))
        }
    }
}
