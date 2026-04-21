package com.github.mrbean355.admiralbulldog.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.mrbean355.admiralbulldog.common.getString

class ProgressHandle(private val entry: DialogEntry) {
    fun dispose() {
        WindowManager.closeDialog(entry)
    }
}

/** Show a progress screen which can't be closed by the user. */
fun showProgressScreen(): ProgressHandle {
    val entry = DialogEntry(getString("title_loading")) {
        Surface {
            Column(
                modifier = Modifier.padding(24.dp).size(200.dp, 150.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
                Text(
                    text = getString("label_loading"),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
    WindowManager.openDialog(entry)
    return ProgressHandle(entry)
}