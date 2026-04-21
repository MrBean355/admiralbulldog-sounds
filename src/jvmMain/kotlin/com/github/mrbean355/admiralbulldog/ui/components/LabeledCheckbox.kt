package com.github.mrbean355.admiralbulldog.ui.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.mrbean355.admiralbulldog.ui.theme.BulldogTheme

@Composable
fun LabeledCheckbox(
    label: String,
    checked: Boolean,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(vertical = 4.dp)
    ) {
        Checkbox(checked = checked, onCheckedChange = onCheckedChange, enabled = enabled)
        Text(label, modifier = Modifier.padding(start = 8.dp))
    }
}

@Preview
@Composable
private fun LabeledCheckboxPreview() {
    BulldogTheme {
        Surface {
            Column(modifier = Modifier.padding(16.dp)) {
                LabeledCheckbox("Checked Option", checked = true, onCheckedChange = {})
                LabeledCheckbox("Unchecked Option", checked = false, onCheckedChange = {})
            }
        }
    }
}
