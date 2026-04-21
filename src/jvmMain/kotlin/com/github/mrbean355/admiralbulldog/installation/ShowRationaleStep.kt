package com.github.mrbean355.admiralbulldog.installation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.github.mrbean355.admiralbulldog.common.WIKI_GAME_FILES
import com.github.mrbean355.admiralbulldog.common.browseUrl
import com.github.mrbean355.admiralbulldog.common.getString

@Composable
fun ShowRationaleStep() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(getString("install_rationale_1"))

        Row {
            Text(getString("install_example_1"))
            Text(
                text = getString("install_example_2"),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        Column {
            Text(getString("install_rationale_2"))
            TextButton(
                onClick = { browseUrl(WIKI_GAME_FILES) },
                contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
            ) {
                Text(getString("install_rationale_3"), color = MaterialTheme.colorScheme.primary)
            }
        }

        Text(getString("install_rationale_4"))
    }
}
