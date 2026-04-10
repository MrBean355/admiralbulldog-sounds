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
import androidx.compose.ui.awt.ComposePanel
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import javafx.scene.control.ButtonType
import java.awt.KeyboardFocusManager
import java.awt.Window
import java.awt.event.KeyEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.JDialog
import javax.swing.SwingUtilities

/**
 * A modal JDialog that displays Compose content.
 * Bridges JavaFX [ButtonType] to Compose actions.
 */
class CommonDialog(
    owner: Window?,
    title: String,
    private val header: String,
    private val content: String?,
    private val icon: @Composable () -> Painter,
    private val buttons: List<ButtonType>,
    private val onButtonClicked: (ButtonType) -> Unit
) : JDialog(owner, title, ModalityType.APPLICATION_MODAL) {

    init {
        val composePanel = ComposePanel()
        composePanel.setContent {
            MaterialTheme {
                Surface {
                    DialogContent()
                }
            }
        }
        contentPane.add(composePanel)
        isResizable = false
        pack()
        setLocationRelativeTo(owner)

        addWindowListener(object : WindowAdapter() {
            override fun windowClosing(e: WindowEvent?) {
                onButtonClicked(ButtonType.CANCEL)
            }
        })

        // Escape to close
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher { event ->
            if (event.id == KeyEvent.KEY_PRESSED && event.keyCode == KeyEvent.VK_ESCAPE && isVisible && isFocused) {
                dispatchEvent(WindowEvent(this, WindowEvent.WINDOW_CLOSING))
                true
            } else false
        }
    }

    @Composable
    private fun DialogContent() {
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
                buttons.forEach { buttonType ->
                    Button(onClick = {
                        onButtonClicked(buttonType)
                        dispose()
                    }) {
                        Text(buttonType.text)
                    }
                }
            }
        }
    }
}

fun showComposeAlert(
    owner: Window?,
    title: String,
    header: String,
    content: String?,
    icon: @Composable () -> Painter,
    buttons: List<ButtonType>,
    actionFn: (ButtonType) -> Unit
) {
    SwingUtilities.invokeLater {
        val dialog = CommonDialog(owner, title, header, content, icon, buttons, actionFn)
        dialog.isVisible = true
    }
}
