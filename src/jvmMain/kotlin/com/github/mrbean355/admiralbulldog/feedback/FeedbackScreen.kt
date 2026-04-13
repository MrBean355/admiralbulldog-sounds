package com.github.mrbean355.admiralbulldog.feedback

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.ui.openComposeScreen
import com.github.mrbean355.admiralbulldog.ui.theme.BulldogTheme

@Composable
fun FeedbackScreen(viewModel: FeedbackViewModel) {
    val rating by viewModel.rating.collectAsState()
    val comments by viewModel.comments.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Column(
        modifier = Modifier.padding(24.dp).width(450.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = getString("label_feedback_info"),
            style = MaterialTheme.typography.bodyLarge
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(getString("label_feedback_rating"), style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                for (i in 1..5) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = rating == i,
                            onClick = { viewModel.setRating(i) },
                            enabled = !isLoading
                        )
                        Text(
                            text = i.toString(),
                            modifier = Modifier.padding(start = 4.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

        OutlinedTextField(
            value = comments,
            onValueChange = { if (it.length <= 1000) viewModel.setComments(it) },
            label = { Text(getString("label_feedback_comments")) },
            placeholder = { Text(getString("prompt_feedback_comments")) },
            modifier = Modifier.fillMaxWidth().height(150.dp),
            maxLines = 5,
            enabled = !isLoading
        )

        Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
            TextButton(
                onClick = { viewModel.requestWindowClose() },
                enabled = !isLoading
            ) {
                Text(getString("btn_no_thanks"))
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = { viewModel.onSubmitClicked() },
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(getString("btn_submit"))
                }
            }
        }
    }
}

// Retain legacy method for invocation points referencing the companion object
object FeedbackScreen {
    fun shouldPrompt(): Boolean = FeedbackViewModel.shouldPrompt()
}


@Preview
@Composable
private fun FeedbackScreenPreview() {
    BulldogTheme {
        Surface {
            FeedbackScreen(FeedbackViewModel())
        }
    }
}

fun openFeedbackScreen() {
    openComposeScreen(
        title = getString("title_feedback"),
        viewModelFactory = { FeedbackViewModel() }
    ) { viewModel ->
        FeedbackScreen(viewModel)
    }
}