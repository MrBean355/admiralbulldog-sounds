package com.github.mrbean355.admiralbulldog.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.awt.ComposePanel
import com.github.mrbean355.admiralbulldog.arch.ComposeViewModel
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.JFrame
import javax.swing.SwingUtilities
import javax.swing.WindowConstants

private val openFrames = mutableMapOf<Class<*>, JFrame>()

fun <VM : ComposeViewModel> openComposeScreen(
    viewModelClass: Class<VM>,
    title: String,
    viewModelFactory: () -> VM,
    content: @Composable (VM) -> Unit
) {
    SwingUtilities.invokeLater {
        val existingFrame = openFrames[viewModelClass]
        if (existingFrame?.isVisible == true) {
            existingFrame.toFront()
            return@invokeLater
        }

        val viewModel = viewModelFactory()
        val frame = JFrame(title).apply {
            val composePanel = ComposePanel()
            composePanel.setContent {
                MaterialTheme {
                    Surface(color = MaterialTheme.colorScheme.background) {
                        content(viewModel)
                    }
                }
            }
            contentPane.add(composePanel)
            isResizable = false
            pack()
            setLocationRelativeTo(null)

            addWindowListener(object : WindowAdapter() {
                override fun windowClosing(e: WindowEvent?) {
                    viewModel.onCleared()
                    openFrames.remove(viewModelClass)
                }
            })

            rootPane.getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0), "closeWindow")
            rootPane.actionMap.put("closeWindow", object : javax.swing.AbstractAction() {
                override fun actionPerformed(e: java.awt.event.ActionEvent?) {
                    dispatchEvent(WindowEvent(this@apply, WindowEvent.WINDOW_CLOSING))
                }
            })

            defaultCloseOperation = WindowConstants.DISPOSE_ON_CLOSE
        }

        openFrames[viewModelClass] = frame
        frame.isVisible = true
    }
}

inline fun <reified VM : ComposeViewModel> openComposeScreen(
    title: String,
    noinline viewModelFactory: () -> VM,
    crossinline content: @Composable (VM) -> Unit
) {
    openComposeScreen(VM::class.java, title, viewModelFactory) { content(it) }
}
