package com.github.mrbean355.admiralbulldog.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import com.github.mrbean355.admiralbulldog.common.AlertButton
import com.github.mrbean355.admiralbulldog.ui.DialogEntry
import com.github.mrbean355.admiralbulldog.ui.WindowManager

fun showComposeAlert(
    title: String,
    header: String,
    content: String?,
    icon: @Composable () -> Painter,
    buttons: List<AlertButton>,
    actionFn: (AlertButton) -> Unit
) {
    WindowManager.openDialog(
        DialogEntry(title) { entry ->
            Surface {
                Column(
                    modifier = Modifier.padding(24.dp).widthIn(min = 300.dp, max = 500.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Image(
                            painter = icon(),
                            contentDescription = null,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = header,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                    content?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp, alignment = Alignment.End)
                    ) {
                        buttons.forEach { alertButton ->
                            Button(onClick = {
                                actionFn(alertButton)
                                WindowManager.closeDialog(entry)
                            }) {
                                Text(alertButton.text)
                            }
                        }
                    }
                }
            }
        }
    )
}
