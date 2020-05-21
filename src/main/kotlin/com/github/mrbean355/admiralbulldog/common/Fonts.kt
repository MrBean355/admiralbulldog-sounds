package com.github.mrbean355.admiralbulldog.common

import javafx.scene.control.Labeled
import javafx.scene.text.Font
import javafx.scene.text.FontWeight

fun Labeled.useSmallFont() {
    font = Font(10.0)
}

fun Labeled.useLargeFont() {
    font = Font(18.0)
}

fun Labeled.useBoldFont() {
    font = Font.font(null, FontWeight.BOLD, -1.0)
}
