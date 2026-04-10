package com.github.mrbean355.admiralbulldog.ui

import javafx.stage.Stage
import tornadofx.Component
import tornadofx.UIComponent

/** Open the component of type [C] as an un-resizable modal. */
inline fun <reified C : UIComponent> Component.openScreen(
    escapeClosesWindow: Boolean = true,
    block: Boolean = false,
): Stage? = find<C>().openModal(
    escapeClosesWindow = escapeClosesWindow,
    block = block,
    resizable = false
)