package com.github.mrbean355.admiralbulldog.installation

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.github.mrbean355.admiralbulldog.common.WINDOW_WIDTH
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.ui.openComposeScreen
import com.github.mrbean355.admiralbulldog.ui.theme.BulldogTheme

@Composable
fun InstallationWizardScreen(viewModel: InstallationWizardViewModel, onCancelled: () -> Unit) {
    val currentStep by viewModel.currentStep.collectAsState()
    val isComplete by viewModel.isComplete.collectAsState()

    Column(
        modifier = Modifier
            .width(WINDOW_WIDTH.dp)
            .height(500.dp)
    ) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = getString("title_app"),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = getString("header_install_gsi"),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // Content
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            when (currentStep) {
                InstallationWizardViewModel.Step.RATIONALE -> ShowRationaleStep()
                InstallationWizardViewModel.Step.DOTA_PATH -> ChooseDotaDirectoryStep(viewModel)
            }
        }

        // Footer
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
        ) {
            TextButton(onClick = onCancelled) {
                Text(getString("btn_cancel"))
            }
            if (currentStep == InstallationWizardViewModel.Step.DOTA_PATH) {
                Button(onClick = { viewModel.previousStep() }) {
                    Text(getString("btn_back"))
                }
            }
            if (currentStep == InstallationWizardViewModel.Step.RATIONALE) {
                Button(onClick = { viewModel.nextStep() }) {
                    Text(getString("btn_next"))
                }
            } else {
                Button(
                    onClick = { viewModel.onFinish() },
                    enabled = isComplete
                ) {
                    Text(getString("btn_finish"))
                }
            }
        }
    }
}

@Preview
@Composable
private fun InstallationWizardScreenPreview() {
    BulldogTheme {
        Surface {
            InstallationWizardScreen(InstallationWizardViewModel()) {}
        }
    }
}

fun openInstallationWizard(onCancelled: () -> Unit = {}) {
    openComposeScreen(
        title = getString("title_app"),
        viewModelFactory = { InstallationWizardViewModel() },
        onCloseRequest = onCancelled
    ) { viewModel ->
        InstallationWizardScreen(viewModel, onCancelled)
    }
}
