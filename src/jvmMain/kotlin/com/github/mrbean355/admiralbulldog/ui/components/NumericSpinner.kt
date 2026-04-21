package com.github.mrbean355.admiralbulldog.ui.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.mrbean355.admiralbulldog.ui.theme.BulldogTheme

@Composable
fun NumericSpinner(
    label: String,
    value: Int,
    range: IntRange,
    enabled: Boolean = true,
    onValueChange: (Int) -> Unit
) {
    var textValue by remember(value) { mutableStateOf(value.toString()) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp).fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(label, modifier = Modifier.weight(1f))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            OutlinedButton(
                onClick = { onValueChange((value - 1).coerceIn(range)) },
                enabled = enabled && value > range.first,
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.size(32.dp)
            ) {
                Text("-", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
            OutlinedTextField(
                value = textValue,
                onValueChange = {
                    textValue = it
                    val newValue = it.toIntOrNull()
                    if (newValue != null && newValue in range) {
                        onValueChange(newValue)
                    }
                },
                enabled = enabled,
                modifier = Modifier.width(80.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            OutlinedButton(
                onClick = { onValueChange((value + 1).coerceIn(range)) },
                enabled = enabled && value < range.last,
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.size(32.dp)
            ) {
                Text("+", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
        }
    }
}

@Preview
@Composable
private fun NumericSpinnerPreview() {
    BulldogTheme {
        Surface {
            Column(modifier = Modifier.padding(16.dp)) {
                NumericSpinner("Test Spinner", value = 50, range = 0..100) {}
            }
        }
    }
}
