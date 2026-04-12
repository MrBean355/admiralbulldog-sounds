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
import androidx.compose.ui.awt.ComposePanel
import androidx.compose.ui.unit.dp
import com.github.mrbean355.admiralbulldog.common.getString
import java.awt.KeyboardFocusManager
import java.awt.Window
import java.awt.event.KeyEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.JDialog
import javax.swing.SwingUtilities

/**
 * A modal progress dialog.
 */
class ProgressDialog(owner: Window?) : JDialog(owner, getString("title_loading"), ModalityType.APPLICATION_MODAL) {

    init {
        val composePanel = ComposePanel()
        composePanel.setContent {
            MaterialTheme {
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
        }
        contentPane.add(composePanel)
        isResizable = false
        pack()
        setLocationRelativeTo(owner)

        // Prevent closing
        addWindowListener(object : WindowAdapter() {
            override fun windowClosing(e: WindowEvent?) {
                // Do nothing
            }
        })

        // Prevent escape closing
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher { event ->
            event.id == KeyEvent.KEY_PRESSED && event.keyCode == KeyEvent.VK_ESCAPE && isVisible && isFocused
        }
    }
}

/** Show a progress screen which can't be closed by the user. */
fun showProgressScreen(owner: Window? = null): ProgressDialog {
    val dialog = ProgressDialog(owner)
    SwingUtilities.invokeLater {
        dialog.isVisible = true
    }
    return dialog
}